package com.werfad.finder;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.werfad.KeyTagsGenerator;
import com.werfad.MarksCanvas;
import com.werfad.UserConfig;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LineFinder implements Finder {
    private static final Pattern pattern = Pattern.compile("(?m)^");

    @Override
    public List<MarksCanvas.Mark> start(Editor e, String s, TextRange visibleRange) {
        int cOffset = e.getCaretModel().getOffset();

        Matcher matcher = pattern.matcher(s);
        List<Integer> offsets = java.util.stream.StreamSupport.stream(matcher.results().spliterator(), false)
            .map(matchResult -> matchResult.start() + visibleRange.getStartOffset())
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