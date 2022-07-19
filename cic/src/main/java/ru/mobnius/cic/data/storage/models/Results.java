package ru.mobnius.cic.data.storage.models;

import com.google.gson.annotations.Expose;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import ru.mobnius.simple_core.data.storage.OnEntityToListener;

import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "cd_results")
public class Results implements OnEntityToListener {
    /**
     * Идентификатор
     */
    @Id
    @Expose
    public String id;

    @Expose
    public String fn_route;


    @Expose
    public String fn_point;

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

    @Expose
    public String jb_data;
    @Expose
    public String c_notice;

    /**
     * Подтверждено
     */
    @Expose
    public boolean b_disabled;

    @Expose
    public Double n_latitude;

    @Expose
    public Double n_longitude;

    @Expose
    public Integer n_distance;

    public boolean __b_reject;
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


    @Generated(hash = 1284869675)
    public Results(String id, String fn_route, String fn_point, long fn_user,
            String d_date, String jb_data, String c_notice, boolean b_disabled,
            Double n_latitude, Double n_longitude, Integer n_distance,
            boolean __b_reject, String objectOperationType, boolean isDelete,
            boolean isSynchronization, String tid, String blockTid) {
        this.id = id;
        this.fn_route = fn_route;
        this.fn_point = fn_point;
        this.fn_user = fn_user;
        this.d_date = d_date;
        this.jb_data = jb_data;
        this.c_notice = c_notice;
        this.b_disabled = b_disabled;
        this.n_latitude = n_latitude;
        this.n_longitude = n_longitude;
        this.n_distance = n_distance;
        this.__b_reject = __b_reject;
        this.objectOperationType = objectOperationType;
        this.isDelete = isDelete;
        this.isSynchronization = isSynchronization;
        this.tid = tid;
        this.blockTid = blockTid;
    }

    @Generated(hash = 991898843)
    public Results() {
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

    public String getFn_point() {
        return this.fn_point;
    }

    public void setFn_point(String fn_point) {
        this.fn_point = fn_point;
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

    public String getJb_data() {
        return this.jb_data;
    }

    public void setJb_data(String jb_data) {
        this.jb_data = jb_data;
    }

    public String getC_notice() {
        return this.c_notice;
    }

    public void setC_notice(String c_notice) {
        this.c_notice = c_notice;
    }

    public boolean getB_disabled() {
        return this.b_disabled;
    }

    public void setB_disabled(boolean b_disabled) {
        this.b_disabled = b_disabled;
    }

    public Double getN_latitude() {
        return this.n_latitude;
    }

    public void setN_latitude(Double n_latitude) {
        this.n_latitude = n_latitude;
    }

    public Double getN_longitude() {
        return this.n_longitude;
    }

    public void setN_longitude(Double n_longitude) {
        this.n_longitude = n_longitude;
    }

    public Integer getN_distance() {
        return this.n_distance;
    }

    public void setN_distance(Integer n_distance) {
        this.n_distance = n_distance;
    }

    public boolean get__b_reject() {
        return this.__b_reject;
    }

    public void set__b_reject(boolean __b_reject) {
        this.__b_reject = __b_reject;
    }

   
}
