package com.goodhealth.contacts;

import java.util.ArrayList;

public class ContactModel {

    private String name, number, id, email;

    private ArrayList<String> PhoneArray = new ArrayList<String>();
    private ArrayList<String> EmailArray = new ArrayList<String>();

    public void setPhoneArray(ArrayList<String> phoneArray) {
        PhoneArray = phoneArray;
    }

    public void setEmailArray(ArrayList<String> emailArray) {
        EmailArray = emailArray;
    }

    public String getName() {
        return name;
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



