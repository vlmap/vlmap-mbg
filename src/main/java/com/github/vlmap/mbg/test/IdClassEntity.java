package com.github.vlmap.mbg.test;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="hbm_tab_idclass")
@IdClass(IdClassKey.class)
public class IdClassEntity implements Serializable {
    @Id
    @Column(name = "a")
    String aa;
    @Id
    @Column(name = "b")
    String bb;
    @Column(name = "c")
    private String cc;
    @Column(name = "d")
    private String dd;

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

    public String getAa() {
        return aa;
    }

    public void setAa(String aa) {
        this.aa = aa;
    }

    public String getBb() {
        return bb;
    }

    public void setBb(String bb) {
        this.bb = bb;
    }
}
