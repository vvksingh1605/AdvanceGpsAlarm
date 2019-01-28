package com.example.mahesh.alarmlocator;


import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by SIS312 on 5/8/2017.
 */

public interface ApiInterface {
    @GET
    Call<ResponseBody> GetAdressApi(@Url String url);
}
