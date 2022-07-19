package ru.mobnius.cic.data.storage.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToOne;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

import ru.mobnius.simple_core.data.storage.OnEntityToListener;

@Entity(nameInDb = "pd_users")
public class Users implements OnEntityToListener {
    
    /**
     * Идентификатор
     */
    @Id
    @Expose
    @Property(nameInDb = "id")
    public Long id;

    /**
     * Родитель
     */
    @Nullable
    public Long f_parent;

    @ToOne(joinProperty = "f_parent")
    public Users parent;

    /**
     * Логин
     */
    @Expose
    public String c_login;

    /**
     * Иконка
     */
    @Expose
    public String fn_file;

    /**
     * Имя
     */
    @Expose
    public String c_fio;

    /**
     * Адрес эл. почты
     */
    @Expose
    public String c_email;

    /**
     * Телефон
     */
    @Expose
    public String c_tel;

    /**
     * Описание
     */
    @Expose
    public String c_description;

    /**
     * Телефон для тревожной кнопки
     */
    @Expose
    public String c_phone;

    /**
     * Отключен
     */
    @Expose
    public boolean b_disabled;

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
    @Generated(hash = 1073488616)
    private transient UsersDao myDao;


    @Generated(hash = 1436476449)
    public Users(Long id, Long f_parent, String c_login, String fn_file,
            String c_fio, String c_email, String c_tel, String c_description,
            String c_phone, boolean b_disabled, String objectOperationType,
            boolean isDelete, boolean isSynchronization, String tid,
            String blockTid) {
        this.id = id;
        this.f_parent = f_parent;
        this.c_login = c_login;
        this.fn_file = fn_file;
        this.c_fio = c_fio;
        this.c_email = c_email;
        this.c_tel = c_tel;
        this.c_description = c_description;
        this.c_phone = c_phone;
        this.b_disabled = b_disabled;
        this.objectOperationType = objectOperationType;
        this.isDelete = isDelete;
        this.isSynchronization = isSynchronization;
        this.tid = tid;
        this.blockTid = blockTid;
    }

    @Generated(hash = 2146996206)
    public Users() {
    }

    @Generated(hash = 1293412156)
    private transient Long parent__resolvedKey;


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

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getF_parent() {
        return this.f_parent;
    }

    public void setF_parent(Long f_parent) {
        this.f_parent = f_parent;
    }

    public String getC_login() {
        return this.c_login;
    }

    public void setC_login(String c_login) {
        this.c_login = c_login;
    }

    public String getFn_file() {
        return this.fn_file;
    }

    public void setFn_file(String fn_file) {
        this.fn_file = fn_file;
    }

    public String getC_fio() {
        return this.c_fio;
    }

    public void setC_fio(String c_fio) {
        this.c_fio = c_fio;
    }

    public String getC_email() {
        return this.c_email;
    }

    public void setC_email(String c_email) {
        this.c_email = c_email;
    }

    public String getC_tel() {
        return this.c_tel;
    }

    public void setC_tel(String c_tel) {
        this.c_tel = c_tel;
    }

    public String getC_description() {
        return this.c_description;
    }

    public void setC_description(String c_description) {
        this.c_description = c_description;
    }

    public String getC_phone() {
        return this.c_phone;
    }

    public void setC_phone(String c_phone) {
        this.c_phone = c_phone;
    }

    public boolean getB_disabled() {
        return this.b_disabled;
    }

    public void setB_disabled(boolean b_disabled) {
        this.b_disabled = b_disabled;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 90685757)
    public Users getParent() {
        Long __key = this.f_parent;
        if (parent__resolvedKey == null || !parent__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UsersDao targetDao = daoSession.getUsersDao();
            Users parentNew = targetDao.load(__key);
            synchronized (this) {
                parent = parentNew;
                parent__resolvedKey = __key;
            }
        }
        return parent;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 546542)
    public void setParent(Users parent) {
        synchronized (this) {
            this.parent = parent;
            f_parent = parent == null ? null : parent.getId();
            parent__resolvedKey = f_parent;
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
    @Generated(hash = 1562339706)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUsersDao() : null;
    }

}
