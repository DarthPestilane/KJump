package com.werfad.action;

import com.werfad.JumpHandler;

public class GotoDeclarationWord1Action extends BaseAction {
    @Override
    public int getMode() {
        return JumpHandler.MODE_WORD1_DECLARATION;
    }
}