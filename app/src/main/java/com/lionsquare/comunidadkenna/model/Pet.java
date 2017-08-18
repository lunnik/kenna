package com.lionsquare.comunidadkenna.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by EDGAR ARANA on 08/08/2017.
 */

public class Pet implements Parcelable {

    @SerializedName("id")
    @Expose
    private int id;
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
    private int reward;
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
    private int type;
    @SerializedName("distance")
    @Expose
    private int distance;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    @SerializedName("user")
    @Expose
    private User user;

    @SerializedName("status")
    @Expose
    private int status;

    public Pet(int id, List<String> images, String nameUser, String namePet, String breed, int reward, String money, String lat, String lng, int type, int distance, String timestamp, User user) {
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
        this.distance = distance;
        this.timestamp = timestamp;
        this.user = user;
    }

    protected Pet(Parcel in) {
        id = in.readInt();
        images = in.createStringArrayList();
        nameUser = in.readString();
        namePet = in.readString();
        breed = in.readString();
        reward = in.readInt();
        money = in.readString();
        lat = in.readString();
        lng = in.readString();
        type = in.readInt();
        distance = in.readInt();
        timestamp = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeStringList(images);
        dest.writeString(nameUser);
        dest.writeString(namePet);
        dest.writeString(breed);
        dest.writeInt(reward);
        dest.writeString(money);
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeInt(type);
        dest.writeInt(distance);
        dest.writeString(timestamp);
        dest.writeParcelable(user, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Pet> CREATOR = new Creator<Pet>() {
        @Override
        public Pet createFromParcel(Parcel in) {
            return new Pet(in);
        }

        @Override
        public Pet[] newArray(int size) {
            return new Pet[size];
        }
    };

    public int getId() {
        return id;
    }

    public List<String> getImages() {
        return images;
    }

    public String getNameUser() {
        return nameUser;
    }

    public String getNamePet() {
        return namePet;
    }

    public String getBreed() {
        return breed;
    }

    public int getReward() {
        return reward;
    }

    public String getMoney() {
        return money;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public int getType() {
        return type;
    }

    public int getDistance() {
        return distance;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public User getUser() {
        return user;
    }

    public int getStatus() {
        return status;
    }
}
