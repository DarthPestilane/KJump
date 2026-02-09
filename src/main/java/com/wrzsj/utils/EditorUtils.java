package com.wrzsj.utils;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.util.TextRange;
import com.intellij.ui.JBColor;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class EditorUtils {
    private static final List<RangeHighlighter> highlighters = new ArrayList<>();

    public static TextRange getVisibleRangeOffset(Editor editor) {
        var scrollingModel = editor.getScrollingModel();
        var visibleArea = scrollingModel.getVisibleArea();
        var startLog = editor.xyToLogicalPosition(new Point(0, visibleArea.y));
        var lastLog = editor.xyToLogicalPosition(new Point(0, visibleArea.y + visibleArea.height));
        var startOff = editor.logicalPositionToOffset(startLog);
        var endOff = editor.logicalPositionToOffset(new LogicalPosition(lastLog.line + 1, lastLog.column));
        return new TextRange(startOff, endOff);
    }

    public static Point offsetToXYCompat(
        Editor editor,
        int offset,
        boolean leanForward,
        boolean beforeSoftWrap
    ) {
        VisualPosition visualPosition = editor.offsetToVisualPosition(offset, leanForward, beforeSoftWrap);
        return editor.visualPositionToXY(visualPosition);
    }

    public static Point offsetToXYCompat(Editor editor, int offset) {
        return offsetToXYCompat(editor, offset, false, false);
    }

    /**
     * 添加灰色覆盖效果，让编辑器文字变灰，提供视觉引导
     */
    public static void addGrayOverlay(Editor editor, boolean currLine) {
        MarkupModel markupModel = editor.getMarkupModel();
        TextAttributes attributes = new TextAttributes();
        attributes.setForegroundColor(JBColor.GRAY);
        TextRange visibleRange = getVisibleRangeOffset(editor);

        int startOffset = visibleRange.getStartOffset();
        int endOffset = visibleRange.getEndOffset();
        if (currLine) {
            int caretOffset = editor.getCaretModel().getOffset();
            startOffset = editor.getDocument().getLineStartOffset(editor.getDocument().getLineNumber(caretOffset));
            endOffset = editor.getDocument().getLineEndOffset(editor.getDocument().getLineNumber(caretOffset));
        }

        RangeHighlighter highlighter = markupModel.addRangeHighlighter(
                 startOffset,
                 endOffset,
                // 0,
                // editor.getDocument().getTextLength(),
                HighlighterLayer.SELECTION - 1,
                attributes,
                currLine ? HighlighterTargetArea.LINES_IN_RANGE : HighlighterTargetArea.EXACT_RANGE
        );
        highlighters.add(highlighter);
    }

    /**
     * 移除灰色覆盖效果
     */
    public static void removeGrayOverlay(Editor editor) {
        MarkupModel markupModel = editor.getMarkupModel();
        for (RangeHighlighter highlighter : highlighters) {
            markupModel.removeHighlighter(highlighter);
        }
        highlighters.clear();
    }
}
