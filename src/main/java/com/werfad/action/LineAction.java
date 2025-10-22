package com.werfad.action;

import com.werfad.JumpHandler;

public class LineAction extends BaseAction {
    @Override
    public int getMode() {
        return JumpHandler.MODE_LINE;
    }
}