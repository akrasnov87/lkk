package ru.mobnius.cic.data.storage.models;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "cs_result_statuses")
public class ResultStatuses {

    /**
     * Идентификатор
     */
    @Id
    @Property(nameInDb = "id")
    public Long id;

    /**
     * Наименование
     */
    public String c_name;

    /**
     * Константа
     */
    public String c_const;

    /**
     * сортировка
     */
    public int n_order;

    @Generated(hash = 1741351707)
    public ResultStatuses(Long id, String c_name, String c_const, int n_order) {
        this.id = id;
        this.c_name = c_name;
        this.c_const = c_const;
        this.n_order = n_order;
    }

    @Generated(hash = 598017987)
    public ResultStatuses() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getC_name() {
        return this.c_name;
    }

    public void setC_name(String c_name) {
        this.c_name = c_name;
    }

    public String getC_const() {
        return this.c_const;
    }

    public void setC_const(String c_const) {
        this.c_const = c_const;
    }

    public int getN_order() {
        return this.n_order;
    }

    public void setN_order(int n_order) {
        this.n_order = n_order;
    }
}
