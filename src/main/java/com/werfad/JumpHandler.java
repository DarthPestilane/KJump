package com.werfad;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.DocCommandGroupId;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.fileEditor.ex.IdeDocumentHistory;
import com.werfad.finder.*;
import com.werfad.utils.EditorUtils;
import com.werfad.utils.ProjectUtils;
import com.werfad.utils.StringUtils;
import com.intellij.openapi.util.TextRange;

import java.util.ArrayList;
import java.util.List;

public class JumpHandler implements TypedActionHandler {
    public static final int MODE_CHAR1 = 0;
    public static final int MODE_CHAR2 = 1;
    public static final int MODE_WORD0 = 2;
    public static final int MODE_WORD1 = 3;
    public static final int MODE_LINE = 4;
    public static final int MODE_WORD1_DECLARATION = 5;
    public static final int MODE_LETTER = 6;

    private static final JumpHandler INSTANCE = new JumpHandler();

    private TypedActionHandler mOldTypedHandler;
    private EditorActionHandler mOldEscActionHandler;
    private final MarksCanvas mMarksCanvas;
    private boolean isStart = false;
    private Finder finder;
    private Runnable onJump; // Runnable that is called after jump
    private List<MarksCanvas.Mark> lastMarks = new ArrayList<>();
    private boolean isCanvasAdded = false;

    private JumpHandler() {
        mMarksCanvas = new MarksCanvas();
    }

    public static JumpHandler getInstance() {
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
            jumpOrShowCanvas(e, lastMarks);
        }
    }

    private final EditorActionHandler escActionHandler = new EditorActionHandler() {
        @Override
        public void doExecute(Editor editor, Caret caret, DataContext dataContext) {
            stop(editor);
        }
    };

    private void jumpOrShowCanvas(Editor e, List<MarksCanvas.Mark> marks) {
        if (marks.isEmpty()) {
            stop(e);
        } else if (marks.size() == 1) {
            // only one found, just jump to it
            Caret caret = e.getCaretModel().getCurrentCaret();
            if (caret.hasSelection()) {
                int downOffset = caret.getSelectionStart() == caret.getOffset()
                    ? caret.getSelectionEnd()
                    : caret.getSelectionStart();
                caret.setSelection(downOffset, marks.get(0).getOffset());
            }
            // Shamelessly robbed from AceJump: https://github.com/acejump/AceJump/blob/99e0a5/src/main/kotlin/org/acejump/action/TagJumper.kt#L87
            if (e.getProject() != null) {
                CommandProcessor.getInstance().executeCommand(
                    e.getProject(), () -> {
                        IdeDocumentHistory history = IdeDocumentHistory.getInstance(e.getProject());
                        history.setCurrentCommandHasMoves();
                        history.includeCurrentCommandAsNavigation();
                        history.includeCurrentPlaceAsChangePlace();
                    }, "KJumpHistoryAppender", DocCommandGroupId.noneGroupId(e.getDocument()),
                    UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION, e.getDocument()
                );
            }
            caret.moveToOffset(marks.get(0).getOffset());

            stop(e);
            if (onJump != null) {
                onJump.run();
            }
        } else {
            if (!isCanvasAdded) {
                mMarksCanvas.sync(e);
                e.getContentComponent().add(mMarksCanvas);
                e.getContentComponent().repaint();
                isCanvasAdded = true;
            }
            mMarksCanvas.setData(marks);
        }
    }

    /**
     * start search mode
     *
     * @param mode mode enum, see [.MODE_CHAR1] [.MODE_CHAR2] etc
     */
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
        onJump = null;

        switch (mode) {
            case MODE_CHAR1:
                finder = new Char1Finder();
                break;
            case MODE_CHAR2:
                finder = new Char2Finder();
                break;
            case MODE_WORD0:
                finder = new Word0Finder();
                break;
            case MODE_WORD1:
                finder = new Word1Finder();
                break;
            case MODE_LINE:
                finder = new LineFinder();
                break;
            case MODE_WORD1_DECLARATION:
                finder = new Word1Finder();
                onJump = () -> {
                    ActionManager.getInstance()
                        .getAction(IdeActions.ACTION_GOTO_DECLARATION)
                        .actionPerformed(anActionEvent);
                };
                break;
            case MODE_LETTER:
                finder = new LetterFinder();
                break;
            default:
                throw new RuntimeException("Invalid start mode: " + mode);
        }

        TextRange visibleBorderOffset = ProjectUtils.getVisibleRangeOffset(editor);
        String visibleString = editor.getDocument().getText(visibleBorderOffset);
        List<MarksCanvas.Mark> marks = finder.start(editor, visibleString, visibleBorderOffset);
        if (marks != null) {
            lastMarks = marks;
            jumpOrShowCanvas(editor, lastMarks);
        }
    }

    private void stop(Editor editor) {
        if (isStart) {
            isStart = false;
            EditorActionManager manager = EditorActionManager.getInstance();
            TypedAction.getInstance().setupRawHandler(mOldTypedHandler);
            if (mOldEscActionHandler != null) {
                manager.setActionHandler(IdeActions.ACTION_EDITOR_ESCAPE, mOldEscActionHandler);
            }

            if (mMarksCanvas.getParent() != null) {
                java.awt.Container parent = mMarksCanvas.getParent();
                parent.remove(mMarksCanvas);
                parent.repaint();
            }
            isCanvasAdded = false;

            EditorUtils.removeGrayOverlay(editor);
        }
    }
}
