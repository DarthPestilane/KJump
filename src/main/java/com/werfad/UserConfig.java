package com.werfad;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;

@State(name = "KJump", storages = @Storage("KJump.xml"))
public class UserConfig implements PersistentStateComponent<UserConfig.DataBean> {
    private final DataBean dataBean = new DataBean();

    @Override
    public DataBean getState() {
        return dataBean;
    }

    @Override
    public void loadState(DataBean dataBean1) {
        XmlSerializerUtil.copyBean(dataBean1, dataBean);
    }

    public static class DataBean {
        public String characters = DEFAULT_CHARACTERS;
        public int backgroundColor = DEFAULT_BG_COLOR;
        public int hit1Color = DEFAULT_FONT_COLOR;
        public int hit2Color0 = DEFAULT_FONT_COLOR;
        public int hit2Color1 = DEFAULT_FONT_COLOR;
    }

    public static final String DEFAULT_CHARACTERS = "hklyuiopnm,qwertzxcvbasdgjf;";
    public static final int DEFAULT_FONT_COLOR = -0x1;
    public static final int DEFAULT_BG_COLOR = -0xff8534;

    private static UserConfig getInstance() {
        return ApplicationManager.getApplication().getService(UserConfig.class);
    }

    public static DataBean getDataBean() {
        return getInstance().dataBean;
    }
}