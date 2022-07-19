package ru.mobnius.cic.data.storage.models;

import com.google.gson.annotations.Expose;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import ru.mobnius.simple_core.data.storage.OnEntityToListener;

@Entity(nameInDb = "sd_client_errors")
public class ClientErrors implements OnEntityToListener {
    /**
     * Идентификатор
     */
    @Id
    @Expose
    public String id;

    /**
     * Текст ошибки
     */
    @Expose
    public String c_message;

    /**
     * код ошибки
     */
    @Expose
    public String c_code;

    /**
     * Дата создания
     */
    @Expose
    public String d_created;

    /**
     * пользователь
     */
    @Expose
    public long fn_user;

    /**
     * версия приложения
     */
    @Expose
    public String c_version;

    /**
     * Платформа
     */
    @Expose
    public String c_platform;

    /**
     * JSON-данные
     */
    @Expose
    public String jb_data;

    public String dx_date;

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

    @Generated(hash = 267717397)
    public ClientErrors(String id, String c_message, String c_code,
            String d_created, long fn_user, String c_version, String c_platform,
            String jb_data, String dx_date, String objectOperationType,
            boolean isDelete, boolean isSynchronization, String tid,
            String blockTid) {
        this.id = id;
        this.c_message = c_message;
        this.c_code = c_code;
        this.d_created = d_created;
        this.fn_user = fn_user;
        this.c_version = c_version;
        this.c_platform = c_platform;
        this.jb_data = jb_data;
        this.dx_date = dx_date;
        this.objectOperationType = objectOperationType;
        this.isDelete = isDelete;
        this.isSynchronization = isSynchronization;
        this.tid = tid;
        this.blockTid = blockTid;
    }

    @Generated(hash = 2056679704)
    public ClientErrors() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getC_message() {
        return this.c_message;
    }

    public void setC_message(String c_message) {
        this.c_message = c_message;
    }

    public String getC_code() {
        return this.c_code;
    }

    public void setC_code(String c_code) {
        this.c_code = c_code;
    }

    public String getD_created() {
        return this.d_created;
    }

    public void setD_created(String d_created) {
        this.d_created = d_created;
    }

    public long getFn_user() {
        return this.fn_user;
    }

    public void setFn_user(long fn_user) {
        this.fn_user = fn_user;
    }

    public String getC_version() {
        return this.c_version;
    }

    public void setC_version(String c_version) {
        this.c_version = c_version;
    }

    public String getC_platform() {
        return this.c_platform;
    }

    public void setC_platform(String c_platform) {
        this.c_platform = c_platform;
    }

    public String getJb_data() {
        return this.jb_data;
    }

    public void setJb_data(String jb_data) {
        this.jb_data = jb_data;
    }

    public String getDx_date() {
        return this.dx_date;
    }

    public void setDx_date(String dx_date) {
        this.dx_date = dx_date;
    }

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
}
