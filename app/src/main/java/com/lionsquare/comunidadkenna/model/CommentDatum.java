package com.lionsquare.comunidadkenna.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by EDGAR ARANA on 18/08/2017.
 */
public class CommentDatum implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("profile_pick")
    @Expose
    private String profilePick;
    @SerializedName("comment")
    @Expose
    private String comment;

    /**
     * No args constructor for use in serialization
     */
    public CommentDatum() {
    }

    /**
     * @param id
     * @param profilePick
     * @param name
     * @param comment
     */
    public CommentDatum(Integer id, String name, String profilePick, String comment) {
        super();
        this.id = id;
        this.name = name;
        this.profilePick = profilePick;
        this.comment = comment;
    }

    protected CommentDatum(Parcel in) {
        id = in.readInt();
        name = in.readString();
        profilePick = in.readString();
        comment = in.readString();
    }

    public static final Creator<CommentDatum> CREATOR = new Creator<CommentDatum>() {
        @Override
        public CommentDatum createFromParcel(Parcel in) {
            return new CommentDatum(in);
        }

        @Override
        public CommentDatum[] newArray(int size) {
            return new CommentDatum[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePick() {
        return profilePick;
    }

    public void setProfilePick(String profilePick) {
        this.profilePick = profilePick;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(profilePick);
        dest.writeString(comment);
    }
}
