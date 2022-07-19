package ru.mobnius.cic.data.storage.models;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "cs_causes")
public class Causes {
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

    /**
     * по умолчанию
     */
    public boolean b_default;

    public boolean b_mobile;

    @Generated(hash = 368813978)
    public Causes(Long id, String c_name, String c_const, int n_order,
            boolean b_default, boolean b_mobile) {
        this.id = id;
        this.c_name = c_name;
        this.c_const = c_const;
        this.n_order = n_order;
        this.b_default = b_default;
        this.b_mobile = b_mobile;
    }

    @Generated(hash = 103144319)
    public Causes() {
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

    public boolean getB_default() {
        return this.b_default;
    }

    public void setB_default(boolean b_default) {
        this.b_default = b_default;
    }

    public boolean getB_mobile() {
        return this.b_mobile;
    }

    public void setB_mobile(boolean b_mobile) {
        this.b_mobile = b_mobile;
    }
}
