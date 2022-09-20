package com.example.e_cartapp.model;

import android.net.Uri;

import java.io.Serializable;

public class UserModel implements Serializable {
    String name, email, password, phone, address, city, country, id;
    Uri profileUrl;

    // Constructor
    public UserModel(String name, String email, String phone, String address, String city, String country, Uri profileUrl) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.country = country;
        this.profileUrl = profileUrl;
    }

    public  UserModel(){

    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Uri getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(Uri profileUrl) {
        this.profileUrl = profileUrl;
    }
}
