package com.wrzsj.finder;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.wrzsj.KeyTagsGenerator;
import com.wrzsj.MarksCanvas;
import com.wrzsj.UserConfig;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LineWordFinder implements Finder {
    // private static final Pattern pattern = Pattern.compile("(?i)\\b\\w");
    private static final Pattern pattern = Pattern.compile("\\b\\w|\\w\\b|(?<=[a-z])(?=[A-Z])\\w|(?<=_)\\w|\\w(?=_)");

    @Override
    public List<MarksCanvas.Mark> start(Editor e, String _s, TextRange _visibleRange) {
        int lineStartOffset = e.getDocument().getLineStartOffset(e.getCaretModel().getLogicalPosition().line);
        int lineEndOffset = e.getDocument().getLineEndOffset(e.getCaretModel().getLogicalPosition().line);
        TextRange lineRange = new TextRange(lineStartOffset, lineEndOffset);
        var lineText = e.getDocument().getText(lineRange);

        int cOffset = e.getCaretModel().getOffset();
        Matcher matcher = pattern.matcher(lineText);

        List<Integer> offsets = java.util.stream.StreamSupport.stream(matcher.results().spliterator(), false)
                .map(matchResult -> matchResult.start() + lineRange.getStartOffset())
                .sorted(Comparator.comparingInt(offset -> Math.abs(offset - cOffset)))
                .collect(Collectors.toList());

        List<String> tags = KeyTagsGenerator.createTagsTree(offsets.size(), UserConfig.getDataBean().characters);

        return IntStream.range(0, Math.min(offsets.size(), tags.size()))
                .mapToObj(i -> new MarksCanvas.Mark(tags.get(i), offsets.get(i)))
                .collect(Collectors.toList());
    }

    @Override
    public List<MarksCanvas.Mark> input(Editor e, char c, List<MarksCanvas.Mark> lastMarks) {
        return advanceMarks(c, lastMarks);
    }
}
