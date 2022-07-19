package ru.mobnius.cic;

import ru.mobnius.simple_core.data.GlobalSettings;
import ru.mobnius.simple_core.data.credentials.BasicCredentials;
import ru.mobnius.simple_core.data.credentials.BasicUser;
import ru.mobnius.cic.data.manager.DataManager;

public abstract class ManagerGenerate extends DbGenerate {
    private final DataManager mDataManager;

    public ManagerGenerate() {
        super();

        mDataManager = DataManager.createInstance(getDaoSession());
        BasicCredentials credentials = getCredentials();

    }


    public DataManager getDataManager() {
        return mDataManager;
    }


    public static String getBaseUrl() {
        return GlobalSettings.getConnectUrl();
    }

    public static BasicCredentials getCredentials() {
        return new BasicCredentials("inspector", "inspector0");
    }

    public static BasicUser getBasicUser() {
        return new BasicUser(getCredentials(), 4, ".inspector.");
    }
}
