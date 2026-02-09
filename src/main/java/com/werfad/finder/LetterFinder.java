package com.werfad.finder;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.werfad.MarksCanvas;
import com.werfad.utils.EditorUtils;
import com.werfad.utils.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class LetterFinder implements Finder {
    private static final int STATE_WAIT_SEARCH_CHAR = 0;
    private static final int STATE_WAIT_KEY = 1;

    private int state = STATE_WAIT_SEARCH_CHAR;
    private String s;
    private TextRange visibleRange;

    @Override
    public List<MarksCanvas.Mark> start(Editor e, String s, TextRange visibleRange) {
        int lineStartOffset = e.getDocument().getLineStartOffset(e.getCaretModel().getLogicalPosition().line);
        int lineEndOffset = e.getDocument().getLineEndOffset(e.getCaretModel().getLogicalPosition().line);
        TextRange lineRange = new TextRange(lineStartOffset, lineEndOffset);

        this.s = e.getDocument().getText(lineRange);
        this.visibleRange = lineRange;
        state = STATE_WAIT_SEARCH_CHAR;

        // 添加灰色覆盖效果，提供视觉引导
        EditorUtils.addGrayOverlay(e, true);

        return null;
    }

    @Override
    public List<MarksCanvas.Mark> input(Editor e, char c, List<MarksCanvas.Mark> lastMarks) {
        switch (state) {
            case STATE_WAIT_SEARCH_CHAR:
                // 移除灰色覆盖效果
                EditorUtils.removeGrayOverlay(e);

                // 找到c所匹配的位置(不区分大小写), 然后高亮这些位置
                List<Integer> offsets = StringUtils.findAll(s, c, true)
                        .stream()
                        .map(offset -> offset + visibleRange.getStartOffset())
                        .collect(Collectors.toList());

                // 返回 marks，使用实际字符作为 keyTag
                String document = e.getDocument().getText();
                List<MarksCanvas.Mark> marks = offsets.stream()
                        .map(offset -> {
                            // 获取该位置的实际字符
                            char actualChar = document.charAt(offset);
                            return new MarksCanvas.Mark(String.valueOf(actualChar), offset);
                        })
                        .collect(Collectors.toList());

                state = STATE_WAIT_KEY;
                return marks;

            case STATE_WAIT_KEY:
                // 如果按 f 则将光标往后跳转到下一个位置，如果按 F 则往前跳转到上一个位置
                if (c == 'f' || c == 'F') {
                    int currentOffset = e.getCaretModel().getOffset();
                    List<Integer> sortedOffsets = lastMarks.stream()
                            .map(MarksCanvas.Mark::getOffset)
                            .sorted()
                            .collect(Collectors.toList());

                    if (c == 'f') {
                        // 向后跳转：找到第一个大于当前光标位置的offset
                        for (int offset : sortedOffsets) {
                            if (offset > currentOffset) {
                                e.getCaretModel().moveToOffset(offset);
                                return lastMarks; // 保持高亮显示
                            }
                        }
                        // 如果没有找到，循环到第一个位置
                        if (!sortedOffsets.isEmpty()) {
                            e.getCaretModel().moveToOffset(sortedOffsets.get(0));
                        }
                    } else { // c == 'F'
                        // 向前跳转：找到最后一个小于当前光标位置的offset
                        for (int i = sortedOffsets.size() - 1; i >= 0; i--) {
                            int offset = sortedOffsets.get(i);
                            if (offset < currentOffset) {
                                e.getCaretModel().moveToOffset(offset);
                                return lastMarks; // 保持高亮显示
                            }
                        }
                        // 如果没有找到，循环到最后一个位置
                        if (!sortedOffsets.isEmpty()) {
                            e.getCaretModel().moveToOffset(sortedOffsets.get(sortedOffsets.size() - 1));
                        }
                    }
                    return lastMarks; // 保持高亮显示
                }
                return null;

            default:
                throw new RuntimeException("Impossible.");
        }
    }
}
