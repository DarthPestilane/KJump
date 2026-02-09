package com.wrzsj.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.wrzsj.GlobalJumpHandler;

public class GlobalWord0Action extends BaseAction {
    @Override
    public int getMode() {
        return GlobalJumpHandler.MODE_WORD0;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        GlobalJumpHandler.start(getMode(), e);
    }
}
