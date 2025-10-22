package com.werfad.utils;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.util.TextRange;

import java.awt.Point;

public class EditorUtils {
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
}