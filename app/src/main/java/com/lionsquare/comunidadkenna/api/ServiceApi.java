package com.lionsquare.comunidadkenna.api;


import com.lionsquare.comunidadkenna.Kenna;
import com.lionsquare.comunidadkenna.model.CheckoutLogin;
import com.lionsquare.comunidadkenna.model.FolioPet;
import com.lionsquare.comunidadkenna.model.ListLost;
import com.lionsquare.comunidadkenna.model.Pet;
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

    // TODO: 08/08/2017  revisa que el email exista en la base de datos
    @FormUrlEncoded
    @POST("checkoutLogin.php")
    Call<CheckoutLogin> checkoutEmail(@Field("email") String email);

    // TODO: 08/08/2017 regista la informacion en la db
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

    // TODO: 08/08/2017 recupera la informacion de user si e s que ya estaba registrado
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

    // TODO: 08/08/2017 actualiza la loc de user
    @FormUrlEncoded
    @POST("updateLoc.php")
    Call<Response> updateLoc(
            @Field("email") String email,
            @Field("token") String token,
            @Field("lat") Double lat,
            @Field("lng") Double lng);

    // TODO: 08/08/2017 se tienes dos archivos de folios un con validacion y otro libre
    @Multipart
    //@POST("lostpet/insertLostPet.php")
    @POST("lostpet/insertLostPetMultiple.php")
    Call<Response> sendReportLostPet(
            @Part("email") RequestBody email,
            @Part("token") RequestBody token,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng,
            @Part("pet") RequestBody pet,
            @Part("breed") RequestBody breed,
            @Part("reward") RequestBody reward,
            @Part("money") RequestBody money,
            @Part List<MultipartBody.Part> files,
            @Part("timestamp") RequestBody timestamp
    );

    // TODO: 08/08/2017 regresa los items en un perimetro de 1 kilometro
    @FormUrlEncoded
    @POST("lostpet/listPetLost/listPetLost.php")
    Call<ListLost> getListPetLost(
            @Field("email") String email,
            @Field("token") String token
    );

    // TODO: 09/08/2017 cnsulta si tiene algun folio activo
    @FormUrlEncoded
    @POST("lostpet/statusLostPet.php")
    Call<Response> checkinStatusFolio(
            @Field("email") String email,
            @Field("token") String token
    );


    @FormUrlEncoded
    @POST("lostpet/comment/insertCommnetLost.php")
    Call<Response> sendCommentPetLost(
            @Field("email") String email,
            @Field("token") String token,
            @Field("id_pet") int id_pet,
            @Field("id_own") int id_own,
            @Field("comment") String comment
    );


    @FormUrlEncoded
    @POST("lostpet/foliosLostPets.php")
    Call<List<FolioPet>> getFolioLostPet(
            @Field("email") String email,
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("lostpet/folioIndividual.php")
    Call<FolioPet> getFolioIndividual(
            @Field("email") String email,
            @Field("token") String token,
            @Field("id") int id
    );

    @FormUrlEncoded
    @POST("lostpet/consultPetIndividual.php")
    Call<Pet> getPetIndividul(
            @Field("email") String email,
            @Field("token") String token,
            @Field("id") int id
    );


}
