package com.life.shelter.people.homeless.Databeas;

/**
 * Created by MahmoudAhmed on 4/9/2018.
 */

public class Product {
    private String id;
    private String homelessName;
    private String homelessAddress;
    private String homelessCity;
    private String downloadimgurl;




    public Product(String homelessName, String homelessAddress, String homelessCity, String downloadimgurl) {
        this.homelessName = homelessName;
        this.homelessAddress = homelessAddress;
        this.homelessCity = homelessCity;
        this.downloadimgurl=downloadimgurl;
    }

    public Product() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return homelessName;
    }

    public void setName(String name) {
        this.homelessName = name;
    }

    public String getAddress() {
        return homelessAddress;
    }

    public void setAddress(String Address) {
        this.homelessAddress = Address;
    }

    public String getCity() {
        return homelessCity;
    }

    public void setCity(String City) {
        this.homelessCity = City;
    }
    public String getDownloadimgurl() {
        return downloadimgurl;
    }

    public void setDownloadimgurl(String downloadimgurl) {
        this.downloadimgurl = downloadimgurl;
    }


}
