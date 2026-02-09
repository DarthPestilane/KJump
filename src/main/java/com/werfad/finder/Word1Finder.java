package com.werfad.finder;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.werfad.KeyTagsGenerator;
import com.werfad.MarksCanvas;
import com.werfad.UserConfig;
import com.werfad.utils.EditorUtils;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Word1Finder implements Finder {
    private static final int STATE_WAIT_SEARCH_CHAR = 0;
    private static final int STATE_WAIT_KEY = 1;

    private int state = STATE_WAIT_SEARCH_CHAR;
    private String s;
    private TextRange visibleRange;

    @Override
    public List<MarksCanvas.Mark> start(Editor e, String s, TextRange visibleRange) {
        this.s = s;
        this.visibleRange = visibleRange;
        state = STATE_WAIT_SEARCH_CHAR;

        // 添加灰色覆盖效果，提供视觉引导
        EditorUtils.addGrayOverlay(e, false);

        return null;
    }

    @Override
    public List<MarksCanvas.Mark> input(Editor e, char c, List<MarksCanvas.Mark> lastMarks) {
        switch (state) {
            case STATE_WAIT_SEARCH_CHAR:
                // 移除灰色覆盖效果
                EditorUtils.removeGrayOverlay(e);

                int cOffset = e.getCaretModel().getOffset();
                String find = Character.isLowerCase(c) ? "(?i)" : "";
                find += "\\b" + Pattern.quote(String.valueOf(c));

                try {
                    Pattern pattern = Pattern.compile(find);
                    Matcher matcher = pattern.matcher(s);

                    List<Integer> offsets = java.util.stream.StreamSupport.stream(matcher.results().spliterator(), false)
                        .map(matchResult -> matchResult.start() + visibleRange.getStartOffset())
                        .sorted(Comparator.comparingInt(offset -> Math.abs(cOffset - offset)))
                        .collect(Collectors.toList());

                    List<String> tags = KeyTagsGenerator.createTagsTree(offsets.size(), UserConfig.getDataBean().characters);
                    state = STATE_WAIT_KEY;

                    return IntStream.range(0, Math.min(offsets.size(), tags.size()))
                        .mapToObj(i -> new MarksCanvas.Mark(tags.get(i), offsets.get(i)))
                        .collect(Collectors.toList());

                } catch (PatternSyntaxException ex) {
                    // Handle regex compilation error
                    return List.of();
                }

            case STATE_WAIT_KEY:
                return advanceMarks(c, lastMarks);

            default:
                throw new RuntimeException("Impossible.");
        }
    }
}
