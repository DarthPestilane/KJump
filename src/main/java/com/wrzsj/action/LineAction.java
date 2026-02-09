package com.wrzsj.action;

import com.wrzsj.JumpHandler;

public class LineAction extends BaseAction {
    @Override
    public int getMode() {
        return JumpHandler.MODE_LINE;
    }
}
