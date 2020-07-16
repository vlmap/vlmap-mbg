package com.github.vlmap.mbg.test;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name="hbm_tab_embedded")
public class EmbeddedIdEntity implements Serializable {
    @EmbeddedId
    private EmbeddedIdKey id;
    @Column(name = "c")

    private String cc;
    @Column(name = "d")

    private String dd;

    public EmbeddedIdKey getId() {
        return id;
    }

    public void setId( EmbeddedIdKey id) {
        this.id = id;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getDd() {
        return dd;
    }

    public void setDd(String dd) {
        this.dd = dd;
    }
}
