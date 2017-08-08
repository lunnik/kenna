package com.lionsquare.comunidadkenna.api;


import com.lionsquare.comunidadkenna.Kenna;
import com.lionsquare.comunidadkenna.model.CheckoutLogin;
import com.lionsquare.comunidadkenna.model.ListLost;
import com.lionsquare.comunidadkenna.model.RecoverProfile;
import com.lionsquare.comunidadkenna.model.Register;
import com.lionsquare.comunidadkenna.model.Response;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
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
    Call<RecoverProfile> recoverProfile(
            @Field("email") String email,
            @Field("token") String token
    );

    // TODO: 31/07/2017 se usa cuando se cambia el tipo de login
    @FormUrlEncoded
    @POST("updateProfile.php")
    Call<RecoverProfile> updateProfile(
            @Field("email") String email,
            @Field("name") String name,
            @Field("profile_pick") String profile_pick,
            @Field("token") String token,
            @Field("type_account") int type_account
    );

    @FormUrlEncoded
    @POST("updateLoc.php")
    Call<Response> updateLoc(
            @Field("email") String email,
            @Field("token") String token,
            @Field("lat") Double lat,
            @Field("lng") Double lng);

    @Multipart
    @POST("lostpet/insertLostPet.php")
    Call<Response> sendReportLostPet(
            @Part("email") RequestBody email,
            @Part("token") RequestBody token,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng,
            @Part("pet") RequestBody pet,
            @Part("breed") RequestBody breed,
            @Part("reward") RequestBody reward,
            @Part("money") RequestBody money,
            @Part List<MultipartBody.Part> files
    );

    @FormUrlEncoded
    @POST("lostpet/listPetLost/listPetLost.php")
    Call<ListLost> getListPetLost(
            @Field("email") String email,
            @Field("token") String token
    );


}
