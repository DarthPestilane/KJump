package com.wrzsj.action;

import com.wrzsj.JumpHandler;

public class LineAnyWhereAction extends BaseAction {
    @Override
    public int getMode() {
        return JumpHandler.MODE_LINE_ANY_WHERE;
    }
}
