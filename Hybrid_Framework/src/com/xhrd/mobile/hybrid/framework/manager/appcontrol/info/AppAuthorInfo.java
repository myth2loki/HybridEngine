package com.xhrd.mobile.hybrid.framework.manager.appcontrol.info;

/**
 * Created by az on 15/12/16.
 */
public class AppAuthorInfo {
    public String name;
    public String email;
    public String tel;
    public String address;


    public AppAuthorInfo(String name, String email, String tel, String address) {
        this.name = name;
        this.email = email;
        this.tel = tel;
        this.address = address;
    }

    @Override
    public String toString() {
        return "RDApplicationAuthorInfo{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", tel='" + tel + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
