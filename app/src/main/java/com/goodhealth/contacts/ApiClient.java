package com.goodhealth.contacts;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    //        public static String BASE_URL = "https://xyz.interwork.io/highMessage/";//AWS
//    public static String BASE_URL = "https://www3.cognitivemobile.net/";    //WWW3
//    public static String BASE_URL = "https://admin.adicyber.com/";    //Admin
    public static String BASE_URL = "https://devhm.adicyber.com/highMessage/";    //devhm
//        public static String BASE_URL = "https://dev2.interwork.io/highMessage/";//Dev2

    //    public static String BASE_URL_FORM = "https://xyz.interwork.io:446/FormIO/api/";//AWS
//    public static String BASE_URL_FORM = "https://www4.cognitivemobile.net/FormIO/api/";    //WWW3
//    public static String BASE_URL_FORM = "https://semic.adicyber.com/FormIO/api/";    //Admin
    public static String BASE_URL_FORM = "https://devsip.adicyber.com/FormIO";    //devhm
    //    public static String BASE_URL_FORM = "https://dev1.interwork.io:446/FormIO/api/";//Dev2

    public static Retrofit retrofit;
    public static Retrofit retrofit_1;

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(1, TimeUnit.MINUTES)
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .addInterceptor(httpLoggingInterceptor)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getClient_Form() {
        if (retrofit_1 == null) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(1, TimeUnit.MINUTES)
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .addInterceptor(httpLoggingInterceptor)
                    .build();
            retrofit_1 = new Retrofit.Builder()
                    .baseUrl(BASE_URL_FORM)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit_1;
    }
}
