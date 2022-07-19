package ru.mobnius.cic.data.storage.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.UUID;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.NotNull;

import ru.mobnius.simple_core.data.storage.OnEntityToListener;
import ru.mobnius.simple_core.data.synchronization.OnAttachmentListeners;

@SuppressWarnings("unused")
@Entity(nameInDb = "attachments")
public class Attachments implements OnEntityToListener, OnAttachmentListeners {

    public Attachments() {
        id = UUID.randomUUID().toString();
    }

    @Generated(hash = 269615198)
    public Attachments(String id, String c_mime, long fn_type, long fn_user,
            String fn_result, String fn_route, String fn_point, String d_date,
            String c_path, double n_longitude, double n_latitude, String c_notice,
            String jb_data, String dx_created, boolean b_disabled, Integer n_size,
            String c_real_file_path, String objectOperationType, boolean isDelete,
            boolean isSynchronization, String tid, String blockTid) {
        this.id = id;
        this.c_mime = c_mime;
        this.fn_type = fn_type;
        this.fn_user = fn_user;
        this.fn_result = fn_result;
        this.fn_route = fn_route;
        this.fn_point = fn_point;
        this.d_date = d_date;
        this.c_path = c_path;
        this.n_longitude = n_longitude;
        this.n_latitude = n_latitude;
        this.c_notice = c_notice;
        this.jb_data = jb_data;
        this.dx_created = dx_created;
        this.b_disabled = b_disabled;
        this.n_size = n_size;
        this.c_real_file_path = c_real_file_path;
        this.objectOperationType = objectOperationType;
        this.isDelete = isDelete;
        this.isSynchronization = isSynchronization;
        this.tid = tid;
        this.blockTid = blockTid;
    }


    /**
     * Идентификатор
     */
    @Id
    @Expose
    public String id;

    @Expose
    public String c_mime;

    /**
     * тип изображения
     */
    @Expose
    public long fn_type;

    @ToOne(joinProperty = "fn_type")
    public AttachmentTypes type;

    /**
     * Идентификатор пользователя
     */
    @Expose
    public long fn_user;

    /**
     * Результат
     */
    @Expose
    public String fn_result;

    @ToOne(joinProperty = "fn_result")
    public Results result;

    /**
     * Маршрут
     */
    @Expose
    public String fn_route;

    @ToOne(joinProperty = "fn_route")
    public Results route;

    /**
     * Точка
     */
    @Expose
    public String fn_point;

    @ToOne(joinProperty = "fn_point")
    public Points point;

    /**
     * Дата создания
     */
    @Expose
    public String d_date;

    /**
     * Имя файла
     */
    @Expose
    public String c_path;

    /**
     * Долгота
     */
    @Expose
    public double n_longitude;

    /**
     * Широта
     */
    @Expose
    public double n_latitude;

    /**
     * Примечание
     */
    @Expose
    public String c_notice;

    @Expose
    public String jb_data;

    @Expose
    public String dx_created;

    @Expose
    public boolean b_disabled;

    @Expose
    public Integer n_size;

    /**
     * Путь к файлу
     */
    public String c_real_file_path;
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
    @Generated(hash = 772926432)
    private transient AttachmentsDao myDao;

    @Generated(hash = 506996655)
    private transient Long type__resolvedKey;

    @Generated(hash = 1347497366)
    private transient String result__resolvedKey;

    @Generated(hash = 603420700)
    private transient String route__resolvedKey;

    @Generated(hash = 2103900843)
    private transient String point__resolvedKey;



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

    @NonNull
    public String getAbsoluteFilePath() {
        return c_real_file_path;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getC_mime() {
        return this.c_mime;
    }

    public void setC_mime(String c_mime) {
        this.c_mime = c_mime;
    }

    public long getFn_type() {
        return this.fn_type;
    }

    public void setFn_type(long fn_type) {
        this.fn_type = fn_type;
    }

    public long getFn_user() {
        return this.fn_user;
    }

    public void setFn_user(long fn_user) {
        this.fn_user = fn_user;
    }

    public String getFn_result() {
        return this.fn_result;
    }

    public void setFn_result(String fn_result) {
        this.fn_result = fn_result;
    }

    public String getFn_route() {
        return this.fn_route;
    }

    public void setFn_route(String fn_route) {
        this.fn_route = fn_route;
    }

    public String getFn_point() {
        return this.fn_point;
    }

    public void setFn_point(String fn_point) {
        this.fn_point = fn_point;
    }

    public String getD_date() {
        return this.d_date;
    }

    public void setD_date(String d_date) {
        this.d_date = d_date;
    }

    public String getC_path() {
        return this.c_path;
    }

    public void setC_path(String c_path) {
        this.c_path = c_path;
    }

    public double getN_longitude() {
        return this.n_longitude;
    }

    public void setN_longitude(double n_longitude) {
        this.n_longitude = n_longitude;
    }

    public double getN_latitude() {
        return this.n_latitude;
    }

    public void setN_latitude(double n_latitude) {
        this.n_latitude = n_latitude;
    }

    public String getC_notice() {
        return this.c_notice;
    }

    public void setC_notice(String c_notice) {
        this.c_notice = c_notice;
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

    public Integer getN_size() {
        return this.n_size;
    }

    public void setN_size(Integer n_size) {
        this.n_size = n_size;
    }

    public String getC_real_file_path() {
        return this.c_real_file_path;
    }

    public void setC_real_file_path(String c_real_file_path) {
        this.c_real_file_path = c_real_file_path;
    }

    public boolean getB_disabled() {
        return this.b_disabled;
    }

    public void setB_disabled(boolean b_disabled) {
        this.b_disabled = b_disabled;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 2067803801)
    public AttachmentTypes getType() {
        long __key = this.fn_type;
        if (type__resolvedKey == null || !type__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AttachmentTypesDao targetDao = daoSession.getAttachmentTypesDao();
            AttachmentTypes typeNew = targetDao.load(__key);
            synchronized (this) {
                type = typeNew;
                type__resolvedKey = __key;
            }
        }
        return type;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1072833871)
    public void setType(@NotNull AttachmentTypes type) {
        if (type == null) {
            throw new DaoException(
                    "To-one property 'fn_type' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.type = type;
            fn_type = type.getId();
            type__resolvedKey = fn_type;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1306757196)
    public Results getResult() {
        String __key = this.fn_result;
        if (result__resolvedKey == null || result__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ResultsDao targetDao = daoSession.getResultsDao();
            Results resultNew = targetDao.load(__key);
            synchronized (this) {
                result = resultNew;
                result__resolvedKey = __key;
            }
        }
        return result;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1153698949)
    public void setResult(Results result) {
        synchronized (this) {
            this.result = result;
            fn_result = result == null ? null : result.getId();
            result__resolvedKey = fn_result;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 613039749)
    public Results getRoute() {
        String __key = this.fn_route;
        if (route__resolvedKey == null || route__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ResultsDao targetDao = daoSession.getResultsDao();
            Results routeNew = targetDao.load(__key);
            synchronized (this) {
                route = routeNew;
                route__resolvedKey = __key;
            }
        }
        return route;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1486388934)
    public void setRoute(Results route) {
        synchronized (this) {
            this.route = route;
            fn_route = route == null ? null : route.getId();
            route__resolvedKey = fn_route;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1154150153)
    public Points getPoint() {
        String __key = this.fn_point;
        if (point__resolvedKey == null || point__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PointsDao targetDao = daoSession.getPointsDao();
            Points pointNew = targetDao.load(__key);
            synchronized (this) {
                point = pointNew;
                point__resolvedKey = __key;
            }
        }
        return point;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1368760242)
    public void setPoint(Points point) {
        synchronized (this) {
            this.point = point;
            fn_point = point == null ? null : point.getId();
            point__resolvedKey = fn_point;
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
    @Generated(hash = 2056440887)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getAttachmentsDao() : null;
    }

}
