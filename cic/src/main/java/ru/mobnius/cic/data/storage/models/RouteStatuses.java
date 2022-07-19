package ru.mobnius.cic.data.storage.models;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "cs_route_statuses")
public class RouteStatuses {
    /**
     * Идентификатор
     */
    @Id
    @Property(nameInDb = "id")
    public Long id;

    /**
     * Константа
     */
    public String c_const;

    /**
     * Наименование
     */
    public String c_name;

    /**
     * Приоритет статуса(чем больше число тем выше статус)
     */
    public Long n_order;

    @Generated(hash = 1513569016)
    public RouteStatuses(Long id, String c_const, String c_name, Long n_order) {
        this.id = id;
        this.c_const = c_const;
        this.c_name = c_name;
        this.n_order = n_order;
    }

    @Generated(hash = 601940321)
    public RouteStatuses() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getC_const() {
        return this.c_const;
    }

    public void setC_const(String c_const) {
        this.c_const = c_const;
    }

    public String getC_name() {
        return this.c_name;
    }

    public void setC_name(String c_name) {
        this.c_name = c_name;
    }

    public Long getN_order() {
        return this.n_order;
    }

    public void setN_order(Long n_order) {
        this.n_order = n_order;
    }

}
