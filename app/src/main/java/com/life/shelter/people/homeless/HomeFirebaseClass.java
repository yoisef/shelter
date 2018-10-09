package com.life.shelter.people.homeless;

import java.io.Serializable;

/**
 * Created by AHMED MAGDY on 9/10/2018.
 */

public class HomeFirebaseClass implements Serializable {
    private String cId;
    private String cName;
    private String cAddress;
    private String cCity;
    private String cUri;
    private String userUri;
    private String username;
    private String pdate;
    private String userid;
    private Boolean checked;
    private String organizationId;
    private String organizationName;


    public  HomeFirebaseClass(){}

    public HomeFirebaseClass(String cId, String cName, String cAddress, String cCity, String cUri,
                             String userUri, String username, String pdate,String userid, Boolean checked, String organizationId, String organizationName) {
        this.cName = cName;
        this.cAddress = cAddress;
        this.cCity = cCity;
        this.cUri = cUri;
        this.userUri = userUri;
        this.username = username;
        this.pdate = pdate;
        this.userid = userid;
        this.cId = cId;
        this.checked = checked;
        this.organizationId = organizationId;
        this.organizationName = organizationName;
    }

    public HomeFirebaseClass(String cId, String cName, String cAddress, String cCity, String cUri,
                             String userUri, String username, String pdate,String userid) {
        this.cName = cName;
        this.cAddress = cAddress;
        this.cCity = cCity;
        this.cUri = cUri;
        this.userUri = userUri;
        this.username = username;
        this.pdate = pdate;
        this.userid = userid;
        this.cId = cId;
    }

    public String getcName() {
        return cName;
    }

    public String getcAddress() {
        return cAddress;
    }

    public String getcCity() {
        return cCity;
    }

    public String getcUri() {
        return cUri;
    }

    public String getUserUri() {
        return userUri;
    }

    public String getUsername() {
        return username;
    }

    public String getPdate() {
        return pdate;
    }

    public String getUserId() {
        return userid;
    }

    public boolean isOwner(String id) {
        return id.equals(getUserId());
    }

    public String getcId() {
        return cId;
    }

    public Boolean getChecked() {
        if (checked == null)
            return false;
        else
            return checked;
    }


    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationName() {
        return organizationName;
    }
}

