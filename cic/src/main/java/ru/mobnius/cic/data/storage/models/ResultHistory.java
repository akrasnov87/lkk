package ru.mobnius.cic.data.storage.models;

import com.google.gson.annotations.Expose;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;

import ru.mobnius.simple_core.data.storage.OnEntityToListener;
import org.greenrobot.greendao.DaoException;

@Entity(nameInDb = "cd_result_history")
public class ResultHistory implements OnEntityToListener {
    /**
     * Идентификатор
     */
    @Id
    @Expose
    public String id;

    @Expose
    public String fn_route;

    @Expose
    public String fn_result;

    @ToOne(joinProperty = "fn_result")
    public Results results;

    @Expose
    public Long fn_status;

    @Expose
    public Long fn_cause;

    @Expose
    public long fn_user;

    @Expose
    public String d_date;

    @Expose
    public String c_notice;

    public String dx_created;
    /**
     * Тип операции надл объектом
     */
    public String objectOperationType;

    /**
     * Запись была удалена или нет
     */
    public boolean isDelete;

    /**
     * Была произведена синхронизация или нет
     */
    public boolean isSynchronization;

    /**
     * идентификатор транзакции
     */
    public String tid;

    /**
     * идентификатор блока
     */
    public String blockTid;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 250628911)
    private transient ResultHistoryDao myDao;


    @Generated(hash = 57834227)
    public ResultHistory(String id, String fn_route, String fn_result,
            Long fn_status, Long fn_cause, long fn_user, String d_date,
            String c_notice, String dx_created, String objectOperationType,
            boolean isDelete, boolean isSynchronization, String tid,
            String blockTid) {
        this.id = id;
        this.fn_route = fn_route;
        this.fn_result = fn_result;
        this.fn_status = fn_status;
        this.fn_cause = fn_cause;
        this.fn_user = fn_user;
        this.d_date = d_date;
        this.c_notice = c_notice;
        this.dx_created = dx_created;
        this.objectOperationType = objectOperationType;
        this.isDelete = isDelete;
        this.isSynchronization = isSynchronization;
        this.tid = tid;
        this.blockTid = blockTid;
    }

    @Generated(hash = 496046610)
    public ResultHistory() {
    }

    @Generated(hash = 597012793)
    private transient String results__resolvedKey;


    @Override
    public String getObjectOperationType() {
        return objectOperationType;
    }

    @Override
    public boolean getIsDelete() {
        return isDelete;
    }

    @Override
    public boolean getIsSynchronization() {
        return isSynchronization;
    }

    @Override
    public String getTid() {
        return tid;
    }

    @Override
    public String getBlockTid() {
        return blockTid;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFn_route() {
        return this.fn_route;
    }

    public void setFn_route(String fn_route) {
        this.fn_route = fn_route;
    }

    public String getFn_result() {
        return this.fn_result;
    }

    public void setFn_result(String fn_result) {
        this.fn_result = fn_result;
    }

    public Long getFn_status() {
        return this.fn_status;
    }

    public void setFn_status(Long fn_status) {
        this.fn_status = fn_status;
    }

    public Long getFn_cause() {
        return this.fn_cause;
    }

    public void setFn_cause(Long fn_cause) {
        this.fn_cause = fn_cause;
    }

    public long getFn_user() {
        return this.fn_user;
    }

    public void setFn_user(long fn_user) {
        this.fn_user = fn_user;
    }

    public String getD_date() {
        return this.d_date;
    }

    public void setD_date(String d_date) {
        this.d_date = d_date;
    }

    public String getC_notice() {
        return this.c_notice;
    }

    public void setC_notice(String c_notice) {
        this.c_notice = c_notice;
    }

    public String getDx_created() {
        return this.dx_created;
    }

    public void setDx_created(String dx_created) {
        this.dx_created = dx_created;
    }

    public void setObjectOperationType(String objectOperationType) {
        this.objectOperationType = objectOperationType;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public void setIsSynchronization(boolean isSynchronization) {
        this.isSynchronization = isSynchronization;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public void setBlockTid(String blockTid) {
        this.blockTid = blockTid;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1283317871)
    public Results getResults() {
        String __key = this.fn_result;
        if (results__resolvedKey == null || results__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ResultsDao targetDao = daoSession.getResultsDao();
            Results resultsNew = targetDao.load(__key);
            synchronized (this) {
                results = resultsNew;
                results__resolvedKey = __key;
            }
        }
        return results;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 130930897)
    public void setResults(Results results) {
        synchronized (this) {
            this.results = results;
            fn_result = results == null ? null : results.getId();
            results__resolvedKey = fn_result;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1361320945)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getResultHistoryDao() : null;
    }
}
