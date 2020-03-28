package org.fjsei.yewu.entity.sdn;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class TestBean {

    private String xxxx;
    //@JsonSerialize(using = JsonConfig.FramworkSerializer.class)
    //@JsonDeserialize(using = JsonConfig.FramworkDeserializer.class)
    @JsonIgnore
    private Framwork framwork;

    public String getXxxx() {
        return xxxx;
    }

    public void setXxxx(String xxxx) {
        this.xxxx = xxxx;
    }

    public Framwork getFramwork() {
        return framwork;
    }

    public void setFramwork(Framwork framwork) {
        this.framwork = framwork;
    }

    @Override
    public String toString() {
        return "TestBean{" +
                "xxxx='" + xxxx + '\'' +
                ", framwork=" + framwork +
                '}';
    }

}
