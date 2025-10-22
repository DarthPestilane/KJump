package com.werfad;

import com.intellij.openapi.options.Configurable;
import com.werfad.UserConfig.DataBean;

import javax.swing.JComponent;

public class KJumpConfigurable implements Configurable {
    private DataBean config;
    private ConfigUI ui;

    @Override
    public boolean isModified() {
        return !ui.getCharacters().equals(config.characters)
            || ui.getHit1FontColor() != config.hit1Color
            || ui.getHit2FontColors() == null
            || ui.getHit2FontColors()[0] != config.hit2Color0
            || ui.getHit2FontColors()[1] != config.hit2Color1
            || ui.getBgColor() != config.backgroundColor;
    }

    @Override
    public String getDisplayName() {
        return "KJump";
    }

    @Override
    public void apply() {
        config.characters = ui.getCharacters();
        int hit1Color = ui.getHit1FontColor();
        config.hit1Color = hit1Color;
        int[] hit2Colors = ui.getHit2FontColors();
        if (hit2Colors == null) {
            config.hit2Color0 = UserConfig.DEFAULT_FONT_COLOR;
            config.hit2Color1 = UserConfig.DEFAULT_FONT_COLOR;
        } else {
            config.hit2Color0 = hit2Colors[0];
            config.hit2Color1 = hit2Colors[1];
        }
        int uiBgColor = ui.getBgColor();
        config.backgroundColor = uiBgColor;
    }

    @Override
    public void reset() {
        fillUI();
    }

    @Override
    public JComponent createComponent() {
        config = UserConfig.getDataBean();
        ui = new ConfigUI();
        fillUI();
        return ui.rootPanel;
    }

    private void fillUI() {
        ui.setCharacters(config.characters);
        ui.setHit1FontColor(config.hit1Color);
        ui.setHit2FontColors(new int[]{config.hit2Color0, config.hit2Color1});
        ui.setBgColor(config.backgroundColor);
    }
}