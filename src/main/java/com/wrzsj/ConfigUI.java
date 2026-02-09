package com.wrzsj;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class ConfigUI {
    public JPanel rootPanel;
    private JTextField hit1ColorTF;
    private JTextField hit2ColorTF0;
    private JTextField hit2ColorTF1;
    private JTextField charactersTF;
    private JTextField bgTF;

    public String getCharacters() {
        return charactersTF.getText();
    }

    public void setCharacters(String s) {
        charactersTF.setText(s);
    }

    public int getHit1FontColor() {
        try {
            return Integer.parseUnsignedInt(hit1ColorTF.getText(), 16);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setHit1FontColor(int c) {
        hit1ColorTF.setText(Integer.toHexString(c));
    }

    public int[] getHit2FontColors() {
        try {
            int color0 = Integer.parseUnsignedInt(hit2ColorTF0.getText(), 16);
            int color1 = Integer.parseUnsignedInt(hit2ColorTF1.getText(), 16);
            return new int[]{color0, color1};
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void setHit2FontColors(int[] arr) {
        if (arr != null) {
            hit2ColorTF0.setText(Integer.toHexString(arr[0]));
            hit2ColorTF1.setText(Integer.toHexString(arr[1]));
        }
    }

    public int getBgColor() {
        try {
            return Integer.parseUnsignedInt(bgTF.getText(), 16);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setBgColor(int c) {
        bgTF.setText(Integer.toHexString(c));
    }
}
