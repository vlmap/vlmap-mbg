package com.github.vlmap.mbg.test;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name="demo",schema = "test")
public class DemoEntity implements Serializable {
    @EmbeddedId
    private Pk id;
    @Column(name = "c")

    private String cc;
    @Column(name = "d")

    private String dd="mmmmmm";

    public  Pk getId() {
        return id;
    }

    public void setId( Pk id) {
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
