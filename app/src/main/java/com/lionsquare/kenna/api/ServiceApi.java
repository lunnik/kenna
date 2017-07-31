package com.lionsquare.kenna.api;


import com.lionsquare.kenna.Kenna;
import com.lionsquare.kenna.model.CheckoutLogin;
import com.lionsquare.kenna.model.RecoverProfile;
import com.lionsquare.kenna.model.Register;
import com.squareup.okhttp.ResponseBody;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
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
            .client(Kenna.httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @FormUrlEncoded
    @POST("checkoutLogin.php")
    Call<CheckoutLogin> checkoutEmail(@Field("email") String email);

    @FormUrlEncoded
    @POST("register.php")
    Call<Register> registerProfile(
            @Field("name") String name,
            @Field("email") String email,
            @Field("profile_pick") String profile_pick,
            @Field("token") String token,
            @Field("type_account") int type_account,
            @Field("lat") Double lat,
            @Field("lng") Double lng);

    @FormUrlEncoded
    @POST("recoverProfile.php")
    Call<RecoverProfile> recoverProfile(@Field("email") String email);

    // TODO: 31/07/2017 se usa cuando se cambia el tipo de login
    @FormUrlEncoded
    @POST("updateProfile.php")
    Call<ResponseBody> updateProfile(
            @Field("email") String email,
            @Field("name") String name,
            @Field("profile_pick") String profile_pick,
            @Field("token") String token,
            @Field("type_account") int type_account
    );


}
