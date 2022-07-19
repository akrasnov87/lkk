package ru.mobnius.cic.data.storage.models;

import com.google.gson.annotations.Expose;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import ru.mobnius.simple_core.data.storage.OnEntityToListener;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Property;

@SuppressWarnings("unused")
@Entity(nameInDb = "ad_audits")
public class Audits implements OnEntityToListener {

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

    public String getC_data() {
        return this.c_data;
    }

    public void setC_data(String c_data) {
        this.c_data = c_data;
    }

    public String getC_type() {
        return this.c_type;
    }

    public void setC_type(String c_type) {
        this.c_type = c_type;
    }

    public String getC_app_name() {
        return this.c_app_name;
    }

    public void setC_app_name(String c_app_name) {
        this.c_app_name = c_app_name;
    }

    public String getDx_created() {
        return this.dx_created;
    }

    public void setDx_created(String dx_created) {
        this.dx_created = dx_created;
    }


    /**
     * Идентификатор
     */
    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    public Long id;

    /**
     * Пользователь
     */
    @Expose
    public long fn_user;

    /**
     * Дата события
     */
    @Expose
    public String d_date;

    /**
     * Дополнительные параметры
     */
    @Expose
    public String c_data;

    /**
     * Тип события
     */
    @Expose
    public String c_type;

    /**
     * Имя приложение
     */
    @Expose
    public String c_app_name;

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

    public String dx_created;

    @Generated(hash = 135384903)
    public Audits(Long id, long fn_user, String d_date, String c_data,
            String c_type, String c_app_name, String objectOperationType,
            boolean isDelete, boolean isSynchronization, String tid,
            String blockTid, String dx_created) {
        this.id = id;
        this.fn_user = fn_user;
        this.d_date = d_date;
        this.c_data = c_data;
        this.c_type = c_type;
        this.c_app_name = c_app_name;
        this.objectOperationType = objectOperationType;
        this.isDelete = isDelete;
        this.isSynchronization = isSynchronization;
        this.tid = tid;
        this.blockTid = blockTid;
        this.dx_created = dx_created;
    }

    @Generated(hash = 2057651182)
    public Audits() {
    }
}
