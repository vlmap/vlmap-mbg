package com.github.vlmap.mbg.test;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class EmbeddedIdKey implements Serializable {

    @Column(name = "a")
    String aa;
    @Column(name = "b")

    String bb;


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
