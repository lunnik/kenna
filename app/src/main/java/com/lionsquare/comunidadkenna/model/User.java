package com.lionsquare.comunidadkenna.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Awesome Pojo Generator
 * */
public class User {
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("profile_pick")
    @Expose
    private String profile_pick;
    private String cover;
    @SerializedName("type_account")
    @Expose
    private String type_account;
    private String token_social;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lng")
    @Expose
    private Double lng;


    public User(int id, String name, String email, String profile_pick, String cover,
                String type_account, String token_social, String token, Double lat, Double lng) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profile_pick = profile_pick;
        this.cover = cover;
        this.type_account = type_account;
        this.token_social = token_social;
        this.token = token;
        this.lat = lat;
        this.lng = lng;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile_pick() {
        return profile_pick;
    }

    public void setProfile_pick(String profile_pick) {
        this.profile_pick = profile_pick;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getType_account() {
        return type_account;
    }

    public void setType_account(String type_account) {
        this.type_account = type_account;
    }

    public String getToken_social() {
        return token_social;
    }

    public void setToken_social(String token_social) {
        this.token_social = token_social;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}