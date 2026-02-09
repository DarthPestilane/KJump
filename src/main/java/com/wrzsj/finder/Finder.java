package com.wrzsj.finder;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.wrzsj.MarksCanvas;

import java.util.List;

public interface Finder {
    /**
     * @return null - need more input to locate.
     *         not null - can be locate some data, empty represent without any matches.
     */
    List<MarksCanvas.Mark> start(Editor e, String s, TextRange visibleRange);

    /**
     * @return same with [.start]
     */
    List<MarksCanvas.Mark> input(Editor e, char c, List<MarksCanvas.Mark> lastMarks);

    /**
     * @return Return the marks whose start character is removed.
     */
    default List<MarksCanvas.Mark> advanceMarks(char c, List<MarksCanvas.Mark> marks) {
        return marks.stream()
            .filter(mark -> mark.getKeyTag().charAt(mark.getAdvanceIndex()) == c)
            .map(mark -> new MarksCanvas.Mark(mark.getKeyTag(), mark.getOffset(), mark.getAdvanceIndex() + 1, null))
            .toList();
    }

    /**
     * @return Return the marks with the start character removed and the editors they belong to.
     */
    default List<MarksCanvas.Mark> advanceGlobalMarks(char c, List<MarksCanvas.Mark> marks) {
        return marks.stream()
            .filter(mark -> mark.getKeyTag().charAt(mark.getAdvanceIndex()) == c)
            .map(mark -> new MarksCanvas.Mark(mark.getKeyTag(), mark.getOffset(), mark.getAdvanceIndex() + 1, mark.getEditor()))
            .toList();
    }
}
