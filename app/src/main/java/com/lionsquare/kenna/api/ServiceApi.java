package com.lionsquare.kenna.api;


import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by edgararana on 24/04/17.
 */

public interface ServiceApi {
    // TODO: 08/11/2016  este objeto retrofit recibe la url general
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://360scripts.com.mx/kenna_v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();


}
