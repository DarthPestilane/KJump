package com.werfad.finder;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.werfad.KeyTagsGenerator;
import com.werfad.MarksCanvas;
import com.werfad.UserConfig;
import com.werfad.utils.EditorUtils;
import com.werfad.utils.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Char2Finder implements Finder {
    private static final int STATE_WAIT_SEARCH_CHAR1 = 0;
    private static final int STATE_WAIT_SEARCH_CHAR2 = 1;
    private static final int STATE_WAIT_KEY = 2;

    private int state = STATE_WAIT_SEARCH_CHAR1;
    private final UserConfig.DataBean config = UserConfig.getDataBean();
    private String s;
    private TextRange visibleRange;
    private char firstChar = ' ';

    @Override
    public List<MarksCanvas.Mark> start(Editor e, String s, TextRange visibleRange) {
        this.s = s;
        this.visibleRange = visibleRange;
        state = STATE_WAIT_SEARCH_CHAR1;

        // 添加灰色覆盖效果，提供视觉引导
        EditorUtils.addGrayOverlay(e, false);

        return null;
    }

    @Override
    public List<MarksCanvas.Mark> input(Editor e, char c, List<MarksCanvas.Mark> lastMarks) {
        switch (state) {
            case STATE_WAIT_SEARCH_CHAR1:
                firstChar = c;
                state = STATE_WAIT_SEARCH_CHAR2;
                return null;

            case STATE_WAIT_SEARCH_CHAR2:
                // 移除灰色覆盖效果
                EditorUtils.removeGrayOverlay(e);

                int caretOffset = e.getCaretModel().getOffset();
                String find = "" + firstChar + c;
                boolean ignoreCase = find.chars().allMatch(ch -> Character.isLowerCase(ch));

                List<Integer> offsets = StringUtils.findAll(s, find, ignoreCase)
                    .stream()
                    .map(offset -> offset + visibleRange.getStartOffset())
                    .sorted(Comparator.comparingInt(offset -> Math.abs(offset - caretOffset)))
                    .collect(Collectors.toList());

                List<String> tags = KeyTagsGenerator.createTagsTree(offsets.size(), config.characters);
                state = STATE_WAIT_KEY;

                return IntStream.range(0, Math.min(offsets.size(), tags.size()))
                    .mapToObj(i -> new MarksCanvas.Mark(tags.get(i), offsets.get(i)))
                    .collect(Collectors.toList());

            case STATE_WAIT_KEY:
                return advanceMarks(c, lastMarks);

            default:
                throw new RuntimeException("Impossible.");
        }
    }
}
