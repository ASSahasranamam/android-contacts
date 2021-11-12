package com.goodhealth.contacts;

import android.util.Log;

import java.util.List;

public class ContactModel {

    private static final String TAG = "ContactModel";
    private String name, number, id, email;

    String[] EmailArray;
    String[]  PhoneArray;

    private String color;


    public void setPhoneArray(String[] phoneArray) {
        Log.d(TAG, "setPhoneArray" + phoneArray);
            PhoneArray = phoneArray;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setEmailArray(String[] emailArray) {
            EmailArray = emailArray;
    }

    public String getName() {
        return name;
    }

    public String[] getEmailArray() {
        return EmailArray;
    }

    public String[] getPhoneArray() {
        return PhoneArray;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}



