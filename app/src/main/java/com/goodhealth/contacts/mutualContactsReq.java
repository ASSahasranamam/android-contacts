package com.goodhealth.contacts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class mutualContactsReq implements Serializable {

    @SerializedName("username")
    @Expose
    private String username;


    @SerializedName("phoneNums")
    @Expose
    private ArrayList<String> phoneNums;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<String> getPhoneNums() {
        return phoneNums;
    }

    public void setPhoneNums(ArrayList<String> phoneNums) {
        this.phoneNums = phoneNums;
    }



}

