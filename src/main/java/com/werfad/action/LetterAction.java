package com.werfad.action;

import com.werfad.JumpHandler;

public class LetterAction extends BaseAction {
    @Override
    public int getMode() {
        return JumpHandler.MODE_LETTER;
    }
}
