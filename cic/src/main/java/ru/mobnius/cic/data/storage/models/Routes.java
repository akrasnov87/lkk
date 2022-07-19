package ru.mobnius.cic.data.storage.models;

import com.google.gson.annotations.Expose;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "cd_routes")
public class Routes {
    /**
     * Идентификатор
     */
    @Id
    @Expose
    public String id;

    /**
     * Номер задания
     */
    @Expose
    public String c_name;

    /**
     * Дата начала выполнения
     */
    @Expose
    public String d_date_start;

    /**
     * Дата завершения выполнения
     */
    @Expose
    public String d_date_end;

    /**
     * Примечание
     */
    @Expose
    public String c_notice;

    /**
     * продлен
     */
    @Expose
    public boolean b_extended;

    /**
     * продлен до
     */
    @Expose
    public String d_extended;


    @Expose
    public String jb_data;

    public String dx_created;

    public int n_order;

    public boolean b_draft;

    @Generated(hash = 519398135)
    public Routes(String id, String c_name, String d_date_start, String d_date_end,
            String c_notice, boolean b_extended, String d_extended, String jb_data,
            String dx_created, int n_order, boolean b_draft) {
        this.id = id;
        this.c_name = c_name;
        this.d_date_start = d_date_start;
        this.d_date_end = d_date_end;
        this.c_notice = c_notice;
        this.b_extended = b_extended;
        this.d_extended = d_extended;
        this.jb_data = jb_data;
        this.dx_created = dx_created;
        this.n_order = n_order;
        this.b_draft = b_draft;
    }

    @Generated(hash = 771875763)
    public Routes() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getC_name() {
        return this.c_name;
    }

    public void setC_name(String c_name) {
        this.c_name = c_name;
    }

    public String getD_date_start() {
        return this.d_date_start;
    }

    public void setD_date_start(String d_date_start) {
        this.d_date_start = d_date_start;
    }

    public String getD_date_end() {
        return this.d_date_end;
    }

    public void setD_date_end(String d_date_end) {
        this.d_date_end = d_date_end;
    }

    public String getC_notice() {
        return this.c_notice;
    }

    public void setC_notice(String c_notice) {
        this.c_notice = c_notice;
    }

    public boolean getB_extended() {
        return this.b_extended;
    }

    public void setB_extended(boolean b_extended) {
        this.b_extended = b_extended;
    }

    public String getD_extended() {
        return this.d_extended;
    }

    public void setD_extended(String d_extended) {
        this.d_extended = d_extended;
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

    public boolean getB_draft() {
        return this.b_draft;
    }

    public void setB_draft(boolean b_draft) {
        this.b_draft = b_draft;
    }

  
}
