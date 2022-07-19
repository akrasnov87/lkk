package ru.mobnius.cic.data.storage.models;

import com.google.gson.annotations.Expose;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;

import org.greenrobot.greendao.DaoException;

import ru.mobnius.simple_core.data.storage.OnEntityToListener;

@Entity(nameInDb = "cd_points")
public class Points implements OnEntityToListener {

    /**
     * Идентификатор
     */
    @Id
    @Expose
    public String id;

    /**
     * Маршрут
     */
    @Expose
    public String f_route;

    @ToOne(joinProperty = "f_route")
    public Routes route;

    @Expose
    public String c_owner;

    @Expose
    public String c_subscr;

    @Expose
    public String c_address;

    @Expose
    public Double n_longitude;

    @Expose
    public Double n_latitude;

    @Expose
    public String jb_data;

    @Expose
    public String dx_created;

    @Expose
    public int n_order;

    @Expose
    public boolean b_person;

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
    @Generated(hash = 617316118)
    private transient PointsDao myDao;


    @Generated(hash = 49384600)
    public Points(String id, String f_route, String c_owner, String c_subscr,
            String c_address, Double n_longitude, Double n_latitude, String jb_data,
            String dx_created, int n_order, boolean b_person,
            String objectOperationType, boolean isDelete, boolean isSynchronization,
            String tid, String blockTid) {
        this.id = id;
        this.f_route = f_route;
        this.c_owner = c_owner;
        this.c_subscr = c_subscr;
        this.c_address = c_address;
        this.n_longitude = n_longitude;
        this.n_latitude = n_latitude;
        this.jb_data = jb_data;
        this.dx_created = dx_created;
        this.n_order = n_order;
        this.b_person = b_person;
        this.objectOperationType = objectOperationType;
        this.isDelete = isDelete;
        this.isSynchronization = isSynchronization;
        this.tid = tid;
        this.blockTid = blockTid;
    }

    @Generated(hash = 1607589943)
    public Points() {
    }

    @Generated(hash = 603420700)
    private transient String route__resolvedKey;


    public String getObjectOperationType() {
        return this.objectOperationType;
    }

    public void setObjectOperationType(String objectOperationType) {
        this.objectOperationType = objectOperationType;
    }

    public boolean getIsDelete() {
        return this.isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public boolean getIsSynchronization() {
        return this.isSynchronization;
    }

    public void setIsSynchronization(boolean isSynchronization) {
        this.isSynchronization = isSynchronization;
    }

    public String getTid() {
        return this.tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getBlockTid() {
        return this.blockTid;
    }

    public void setBlockTid(String blockTid) {
        this.blockTid = blockTid;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getF_route() {
        return this.f_route;
    }

    public void setF_route(String f_route) {
        this.f_route = f_route;
    }

    public String getC_owner() {
        return this.c_owner;
    }

    public void setC_owner(String c_owner) {
        this.c_owner = c_owner;
    }

    public String getC_subscr() {
        return this.c_subscr;
    }

    public void setC_subscr(String c_subscr) {
        this.c_subscr = c_subscr;
    }

    public String getC_address() {
        return this.c_address;
    }

    public void setC_address(String c_address) {
        this.c_address = c_address;
    }

    public Double getN_longitude() {
        return this.n_longitude;
    }

    public void setN_longitude(Double n_longitude) {
        this.n_longitude = n_longitude;
    }

    public Double getN_latitude() {
        return this.n_latitude;
    }

    public void setN_latitude(Double n_latitude) {
        this.n_latitude = n_latitude;
    }

    public String getJb_data() {
        return this.jb_data;
    }

    public void setJb_data(String jb_data) {
        this.jb_data = jb_data;
    }

    public String getDx_created() {
        return this.dx_created;
    }

    public void setDx_created(String dx_created) {
        this.dx_created = dx_created;
    }

    public int getN_order() {
        return this.n_order;
    }

    public void setN_order(int n_order) {
        this.n_order = n_order;
    }

    public boolean getB_person() {
        return this.b_person;
    }

    public void setB_person(boolean b_person) {
        this.b_person = b_person;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1667685995)
    public Routes getRoute() {
        String __key = this.f_route;
        if (route__resolvedKey == null || route__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            RoutesDao targetDao = daoSession.getRoutesDao();
            Routes routeNew = targetDao.load(__key);
            synchronized (this) {
                route = routeNew;
                route__resolvedKey = __key;
            }
        }
        return route;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 610281170)
    public void setRoute(Routes route) {
        synchronized (this) {
            this.route = route;
            f_route = route == null ? null : route.getId();
            route__resolvedKey = f_route;
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
    @Generated(hash = 596903735)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPointsDao() : null;
    }

}
