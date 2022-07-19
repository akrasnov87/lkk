package ru.mobnius.cic.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ru.mobnius.cic.data.manager.DataManager
import ru.mobnius.cic.MobniusApplication
import ru.mobnius.cic.data.storage.DbOpenHelper
import ru.mobnius.cic.data.storage.models.DaoMaster
import ru.mobnius.simple_core.data.credentials.BasicUser
import ru.mobnius.simple_core.preferences.GeneralPreferences

class ClientErrorsWork(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        if (GeneralPreferences.getInstance() == null) {
            GeneralPreferences.createInstance(applicationContext);
        }
        GeneralPreferences.getInstance()?.let {
            if (it.isSingleUser) {

            }
        }
        if (DataManager.getInstance() == null) {
        }
        return Result.success()
    }


    fun sendData(user: BasicUser){
        if (DataManager.getInstance() == null) {
            val daoSession = DaoMaster(
                DbOpenHelper(
                    applicationContext,
                    MobniusApplication.USER_ENG + user.userId + ".db"
                ).writableDb
            ).newSession()
            daoSession.clear()
            DataManager.createInstance(daoSession)
        }
        DataManager.getInstance()?.let {
            val list = it.daoSession.auditsDao.loadAll()
            list?.let {

            }
        }
    }
}