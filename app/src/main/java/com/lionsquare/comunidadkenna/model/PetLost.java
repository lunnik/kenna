package com.lionsquare.comunidadkenna.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by EDGAR ARANA on 08/08/2017.
 */

public class PetLost {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("images")
    @Expose
    private List<String> images = null;
    @SerializedName("name_user")
    @Expose
    private String nameUser;
    @SerializedName("name_pet")
    @Expose
    private String namePet;
    @SerializedName("breed")
    @Expose
    private String breed;
    @SerializedName("reward")
    @Expose
    private String reward;
    @SerializedName("money")
    @Expose
    private String money;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("user")
    @Expose
    private User user;


    /**
     *
     * @param id
     * @param breed
     * @param nameUser
     * @param reward
     * @param money
     * @param images
     * @param lng
     * @param type
     * @param namePet
     * @param user
     * @param lat
     */
    public PetLost(String id, List<String> images, String nameUser, String namePet, String breed, String reward, String money, String lat, String lng, Integer type, User user) {
        super();
        this.id = id;
        this.images = images;
        this.nameUser = nameUser;
        this.namePet = namePet;
        this.breed = breed;
        this.reward = reward;
        this.money = money;
        this.lat = lat;
        this.lng = lng;
        this.type = type;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getNamePet() {
        return namePet;
    }

    public void setNamePet(String namePet) {
        this.namePet = namePet;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
