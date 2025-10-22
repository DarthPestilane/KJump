package com.werfad.finder;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.werfad.MarksCanvas;
import com.werfad.utils.ProjectUtils;

import java.util.List;
import java.util.regex.Pattern;

public class GlobalWord0Finder implements Finder {
    private static final Pattern pattern = Pattern.compile("(?i)\\b\\w");

    @Override
    public List<MarksCanvas.Mark> start(Editor e, String s, TextRange visibleRange) {
        return ProjectUtils.getMarksFromAllEditors(e.getProject(), pattern);
    }

    @Override
    public List<MarksCanvas.Mark> input(Editor e, char c, List<MarksCanvas.Mark> lastMarks) {
        return advanceGlobalMarks(c, lastMarks);
    }
}