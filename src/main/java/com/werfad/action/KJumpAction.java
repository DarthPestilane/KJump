package com.werfad.action;

import com.werfad.JumpHandler;

public class KJumpAction extends BaseAction {
    @Override
    public int getMode() {
        return JumpHandler.MODE_CHAR1;
    }
}