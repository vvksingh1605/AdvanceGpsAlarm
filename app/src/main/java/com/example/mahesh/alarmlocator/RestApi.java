package com.example.mahesh.alarmlocator;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

/**
 * Created by SIS312 on 11/16/2016.
 */

public class RestApi {
    // all web api goes here

    //create an object of SingleObject

    private static ApiInterface apiinterfaceinstance;


    //make the constructor private so that this class cannot be
    //instantiated
    private RestApi() {

    }

    public static ApiInterface getInstance() {


        // set your desired log level
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        //logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …
        // add logging as last interceptor
        //httpClient.addInterceptor(logging);
        // <-- this is the important lin
        httpClient.readTimeout(120, TimeUnit.SECONDS);
        httpClient.connectTimeout(120, TimeUnit.SECONDS);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://projects.thesparxitsolutions.com/SIS364/api/".trim())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();
        // initialising retrofit
        apiinterfaceinstance =
                retrofit.create(ApiInterface.class);
        return apiinterfaceinstance;
    }







}
