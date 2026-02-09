package com.wrzsj.action;

import com.wrzsj.JumpHandler;

public class KJumpAction extends BaseAction {
    @Override
    public int getMode() {
        return JumpHandler.MODE_CHAR1;
    }
}
