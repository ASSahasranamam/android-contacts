package com.goodhealth.contacts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class mutualContactsres implements Serializable {

    @SerializedName("mutualDocs")
    @Expose
    private ArrayList<String> mutualDocs;

    public ArrayList<String> getMutualDocs() {




        return mutualDocs;
    }




    public void setMutualDocs(ArrayList<String> mutualDocs) {
        this.mutualDocs = mutualDocs;
    }
}


