package com.wrzsj;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.*;
import com.intellij.openapi.fileEditor.ex.IdeDocumentHistory;
import com.wrzsj.finder.*;
import com.wrzsj.utils.ProjectUtils;
import com.intellij.openapi.util.TextRange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalJumpHandler implements TypedActionHandler {
    public static final int MODE_WORD0 = 2;

    private static final GlobalJumpHandler INSTANCE = new GlobalJumpHandler();

    private TypedActionHandler mOldTypedHandler;
    private EditorActionHandler mOldEscActionHandler;
    private boolean isStart = false;
    private Finder finder;
    private Map<Editor, MarksCanvas> mMarksCanvasMap = new HashMap<>();
    private List<MarksCanvas.Mark> lastMarks = new ArrayList<>();

    private GlobalJumpHandler() {
    }

    public static GlobalJumpHandler getInstance() {
        return INSTANCE;
    }

    public static void start(int mode, AnActionEvent anActionEvent) {
        INSTANCE.startInstance(mode, anActionEvent);
    }

    @Override
    public void execute(Editor e, char c, DataContext dc) {
        List<MarksCanvas.Mark> marks = finder.input(e, c, lastMarks);
        if (marks != null) {
            lastMarks = marks;
            jumpOrShowCanvas(lastMarks);
        }
    }

    private final EditorActionHandler escActionHandler = new EditorActionHandler() {
        @Override
        public void doExecute(Editor editor, Caret caret, DataContext dataContext) {
            stop();
        }
    };

    private void jumpOrShowCanvas(List<MarksCanvas.Mark> marks) {
        if (marks.isEmpty()) {
            stop();
        } else if (marks.size() == 1) {
            jumpToMark(marks.get(0));
        } else {
            handleMultipleMarks(marks);
        }
    }

    private void jumpToMark(MarksCanvas.Mark mark) {
        if (mark.getEditor() != null) {
            Editor editor = mark.getEditor();
            editor.getContentComponent().requestFocus();
            Caret caret = editor.getCaretModel().getCurrentCaret();
            if (caret.hasSelection()) {
                int downOffset = caret.getSelectionStart() == caret.getOffset()
                    ? caret.getSelectionEnd()
                    : caret.getSelectionStart();
                caret.setSelection(downOffset, mark.getOffset());
            }
            // Shamelessly robbed from AceJump: https://github.com/acejump/AceJump/blob/99e0a5/src/main/kotlin/org/acejump/action/TagJumper.kt#L87
            CommandProcessor.getInstance().executeCommand(
                editor.getProject(), () -> {
                        IdeDocumentHistory history = IdeDocumentHistory.getInstance(editor.getProject());
                        history.setCurrentCommandHasMoves();
                        history.includeCurrentCommandAsNavigation();
                        history.includeCurrentPlaceAsChangePlace();
                }, "KJumpHistoryAppender", DocCommandGroupId.noneGroupId(editor.getDocument()),
                UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION, editor.getDocument()
            );
            caret.moveToOffset(mark.getOffset());
            stop();
        }
    }

    /**
     * Processes marks by grouping them by editor, updating their canvases,
     * and removing canvases where tags are not found.
     */
    private void handleMultipleMarks(List<MarksCanvas.Mark> marks) {
        Map<Editor, List<MarksCanvas.Mark>> marksByEditor = new HashMap<>();
        for (MarksCanvas.Mark mark : marks) {
            marksByEditor.computeIfAbsent(mark.getEditor(), k -> new ArrayList<>()).add(mark);
        }

        for (Map.Entry<Editor, List<MarksCanvas.Mark>> entry : marksByEditor.entrySet()) {
            Editor editor = entry.getKey();
            List<MarksCanvas.Mark> editorMarks = entry.getValue();

            if (editor != null) {
                MarksCanvas canvas = mMarksCanvasMap.computeIfAbsent(editor, e -> {
                    MarksCanvas newCanvas = new MarksCanvas();
                    newCanvas.sync(e);
                    e.getContentComponent().add(newCanvas);
                    newCanvas.revalidate();
                    newCanvas.repaint();
                    return newCanvas;
                });

                List<MarksCanvas.Mark> canvasMarks = new ArrayList<>();
                for (MarksCanvas.Mark mark : editorMarks) {
                    MarksCanvas.Mark canvasMark = new MarksCanvas.Mark(
                        mark.getKeyTag().substring(mark.getAdvanceIndex()),
                        mark.getOffset(),
                        mark.getEditor()
                    );
                    canvasMarks.add(canvasMark);
                }

                canvas.setData(canvasMarks);
                canvas.repaint();
                editor.getContentComponent().revalidate();
                editor.getContentComponent().repaint();
            }
        }

        // Remove canvases for inactive editors
        for (Editor editor : new ArrayList<>(mMarksCanvasMap.keySet())) {
            if (!marks.stream().anyMatch(mark -> mark.getEditor() == editor)) {
                MarksCanvas canvas = mMarksCanvasMap.remove(editor);
                if (canvas != null) {
                    editor.getContentComponent().remove(canvas);
                    editor.getContentComponent().repaint();
                }
            }
        }
    }

    private void startInstance(int mode, AnActionEvent anActionEvent) {
        if (isStart) return;
        isStart = true;
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        if (editor == null) return;

        EditorActionManager manager = EditorActionManager.getInstance();
        TypedAction typedAction = TypedAction.getInstance();
        mOldTypedHandler = typedAction.getRawHandler();
        typedAction.setupRawHandler(this);
        mOldEscActionHandler = manager.getActionHandler(IdeActions.ACTION_EDITOR_ESCAPE);
        manager.setActionHandler(IdeActions.ACTION_EDITOR_ESCAPE, escActionHandler);

        TextRange visibleBorderOffset = ProjectUtils.getVisibleRangeOffset(editor);
        String visibleString = editor.getDocument().getText(visibleBorderOffset);

        switch (mode) {
            case MODE_WORD0:
                finder = new GlobalWord0Finder();
                break;
            default:
                throw new RuntimeException("Invalid start mode: " + mode);
        }

        List<MarksCanvas.Mark> marks = finder.start(editor, visibleString, visibleBorderOffset);
        if (marks != null) {
            lastMarks = marks;
            jumpOrShowCanvas(lastMarks);
        }
    }

    private void stop() {
        if (isStart) {
            isStart = false;
            EditorActionManager manager = EditorActionManager.getInstance();
            TypedAction.getInstance().setupRawHandler(mOldTypedHandler);
            if (mOldEscActionHandler != null) {
                manager.setActionHandler(IdeActions.ACTION_EDITOR_ESCAPE, mOldEscActionHandler);
            }

            for (Map.Entry<Editor, MarksCanvas> entry : mMarksCanvasMap.entrySet()) {
                Editor editor = entry.getKey();
                MarksCanvas canvas = entry.getValue();
                editor.getContentComponent().remove(canvas);
                editor.getContentComponent().repaint();
            }
            mMarksCanvasMap.clear();
        }
    }
}
