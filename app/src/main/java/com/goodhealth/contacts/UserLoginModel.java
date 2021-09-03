package com.goodhealth.contacts;

import java.util.ArrayList;

public class UserLoginModel {

    private String name,  id, phNumber, password;

    private ArrayList<String> PhoneArray = new ArrayList<String>();
    private ArrayList<String> EmailArray = new ArrayList<String>();

    public void setPhoneArray(ArrayList<String> phoneArray) {
        PhoneArray = phoneArray;
    }

    public void setEmailArray(ArrayList<String> emailArray) {
        EmailArray = emailArray;
    }

    public boolean register(String name, String number, String password)
    {
        return true;
    };

    public boolean Login( String number, String password) {
        return true;
    }

        public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhNumber() {
        return phNumber;
    }


    public void setNumber(String phNumber) {
        this.phNumber = phNumber;
    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
