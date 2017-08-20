package com.lionsquare.comunidadkenna.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by EDGAR ARANA on 18/08/2017.
 */

public class FolioPet {
    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("comment_data")
    @Expose
    private List<CommentDatum> commentData = null;
    @SerializedName("pet")
    @Expose
    private Pet pet;

    /**
     * No args constructor for use in serialization
     *
     */
    public FolioPet() {
    }

    /**
     *
     * @param commentData
     * @param message
     * @param success
     */
    public FolioPet(Integer success, String message, List<CommentDatum> commentData) {
        super();
        this.success = success;
        this.message = message;
        this.commentData = commentData;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<CommentDatum> getCommentData() {
        return commentData;
    }

    public void setCommentData(List<CommentDatum> commentData) {
        this.commentData = commentData;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }
}
