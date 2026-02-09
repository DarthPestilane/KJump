package com.wrzsj.action;

import com.wrzsj.JumpHandler;

public class GotoDeclarationWord1Action extends BaseAction {
    @Override
    public int getMode() {
        return JumpHandler.MODE_WORD1_DECLARATION;
    }
}
