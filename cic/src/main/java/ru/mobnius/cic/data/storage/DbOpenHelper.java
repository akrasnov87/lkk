package ru.mobnius.cic.data.storage;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

import ru.mobnius.cic.data.storage.models.DaoMaster;

public class DbOpenHelper extends DaoMaster.OpenHelper {

    public DbOpenHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }
}