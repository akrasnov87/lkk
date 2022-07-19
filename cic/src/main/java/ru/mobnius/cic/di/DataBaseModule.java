package ru.mobnius.cic.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.mobnius.cic.MobniusApplication;
import ru.mobnius.cic.data.storage.DbOpenHelper;
import ru.mobnius.cic.data.storage.models.DaoMaster;
import ru.mobnius.cic.data.storage.models.DaoSession;

@Module
public class DataBaseModule {

    @Singleton
    @Provides
    DaoSession provideDaoSession(DaoMaster daoMaster) {
        final DaoSession daoSession = daoMaster.newSession();
        daoSession.clear();
        return daoSession;
    }

    @Singleton
    @Provides
    DbOpenHelper provideDbOpenHelper(Context context, long userId) {
        return new DbOpenHelper(context, MobniusApplication.USER_ENG + userId + ".db");
    }

    @Singleton
    @Provides
    DaoMaster provideDaoMaster(DbOpenHelper dbOpenHelper) {
        return new DaoMaster(dbOpenHelper.getWritableDb());
    }


}
