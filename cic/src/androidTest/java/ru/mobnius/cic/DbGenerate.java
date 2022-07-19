package ru.mobnius.cic;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import ru.mobnius.cic.data.storage.DbOpenHelper;
import ru.mobnius.cic.data.storage.models.DaoMaster;
import ru.mobnius.cic.data.storage.models.DaoSession;

/**
 * Вспомогательный класс для работы с БД
 */
public abstract class DbGenerate {
    private final Context mContext;
    private final DaoSession mDaoSession;

    public DbGenerate() {
        String dbName = getClass().getName();
        mContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mDaoSession = new DaoMaster(new DbOpenHelper(mContext, dbName).getWritableDb()).newSession();

    }

    /**
     * получение ссылки на подключение к БД
     * @return объект DaoSession
     */
    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public Context getContext() {
        return mContext;
    }
}
