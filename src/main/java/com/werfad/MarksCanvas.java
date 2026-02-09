package com.werfad;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.werfad.UserConfig.DataBean;
import com.werfad.utils.EditorUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class MarksCanvas extends JComponent {
    private List<Mark> mMarks = List.of();
    private Editor mEditor;
    private Font mFont;
    private FontMetrics mFontMetrics;

    public void sync(Editor e) {
        Rectangle visibleArea = e.getScrollingModel().getVisibleArea();
        setBounds(visibleArea.x, visibleArea.y, visibleArea.width, visibleArea.height);
        mEditor = e;
        mFont = e.getColorsScheme().getFont(EditorFontType.BOLD);
        mFontMetrics = e.getContentComponent().getFontMetrics(mFont);
    }

    public void setData(List<Mark> marks) {
        mMarks = marks;
        repaint();
    }

    private DataBean getConfig() {
        return UserConfig.getDataBean();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        List<Point> coordinates = mMarks.stream()
            .map(mark -> EditorUtils.offsetToXYCompat(mEditor, mark.getOffset()))
            .collect(Collectors.toList());

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

        List<MarkCoordinatePair> pairs = new java.util.ArrayList<>();
        for (int i = 0; i < mMarks.size(); i++) {
            pairs.add(new MarkCoordinatePair(mMarks.get(i), coordinates.get(i)));
        }

        pairs.stream()
            .sorted((a, b) -> Integer.compare(b.getCoordinate().x, a.getCoordinate().x))
            .forEach(pair -> {
                Mark mark = pair.getMark();
                Point coordinate = pair.getCoordinate();

                g2d.setColor(new Color(getConfig().backgroundColor, true));
                String keyTag = mark.getKeyTag();
                Rectangle bounds = mFontMetrics.getStringBounds(
                    keyTag.substring(mark.getAdvanceIndex()), g
                ).getBounds();

                g2d.fillRect(coordinate.x - getX(), coordinate.y - getY(), bounds.width, bounds.height);
                g2d.setFont(mFont);

                int xInCanvas = coordinate.x - getX();
                int yInCanvas = coordinate.y - getY() + bounds.height - mFontMetrics.getDescent();
                if (keyTag.length() == 2) {
                    if (mark.getAdvanceIndex() == 0) {
                        int midX = xInCanvas + bounds.width / 2;

                        // first char
                        g2d.setColor(new Color(getConfig().hit2Color0, true));
                        g2d.drawString(String.valueOf(keyTag.charAt(0)), xInCanvas, yInCanvas);

                        // second char
                        g2d.setColor(new Color(getConfig().hit2Color1, true));
                        g2d.drawString(String.valueOf(keyTag.charAt(1)), midX, yInCanvas);
                    } else {
                        g2d.setColor(new Color(getConfig().hit2Color1, true));
                        g2d.drawString(String.valueOf(keyTag.charAt(1)), xInCanvas, yInCanvas);
                    }
                } else {
                    g2d.setColor(new Color(getConfig().hit1Color, true));
                    g2d.drawString(String.valueOf(keyTag.charAt(0)), xInCanvas, yInCanvas);
                }
            });
        super.paint(g);
    }

    public static class Mark {
        private final String keyTag;
        private final int offset;
        private final int advanceIndex;
        private final Editor editor;

        public Mark(String keyTag, int offset, int advanceIndex, Editor editor) {
            this.keyTag = keyTag;
            this.offset = offset;
            this.advanceIndex = advanceIndex;
            this.editor = editor;
        }

        public Mark(String keyTag, int offset, Editor editor) {
            this(keyTag, offset, 0, editor);
        }

        public Mark(String keyTag, int offset) {
            this(keyTag, offset, 0, null);
        }

        public String getKeyTag() {
            return keyTag;
        }

        public int getOffset() {
            return offset;
        }

        public int getAdvanceIndex() {
            return advanceIndex;
        }

        public Editor getEditor() {
            return editor;
        }
    }

    private static class MarkCoordinatePair {
        private final Mark mark;
        private final Point coordinate;

        public MarkCoordinatePair(Mark mark, Point coordinate) {
            this.mark = mark;
            this.coordinate = coordinate;
        }

        public Mark getMark() {
            return mark;
        }

        public Point getCoordinate() {
            return coordinate;
        }
    }
}
