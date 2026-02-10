package com.wrzsj.action;

import com.wrzsj.JumpHandler;

public class LineCharAction extends BaseAction {
    @Override
    public int getMode() {
        return JumpHandler.MODE_LINE_CHAR;
    }
}
