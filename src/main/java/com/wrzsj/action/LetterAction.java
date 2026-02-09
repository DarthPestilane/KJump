package com.wrzsj.action;

import com.wrzsj.JumpHandler;

public class LetterAction extends BaseAction {
    @Override
    public int getMode() {
        return JumpHandler.MODE_LETTER;
    }
}
