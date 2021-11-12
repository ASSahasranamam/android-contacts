package com.goodhealth.contacts;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("mutualcontacts")
    Call<mutualContactsres> getMutualContactsDocs(@Body mutualContactsReq obj);
}

