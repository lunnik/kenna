package com.lionsquare.comunidadkenna.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by EDGAR ARANA on 02/08/2017.
 */

public class Response {


    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("message")
    @Expose
    private String message;



    public Response( Integer success, String message) {

        this.success = success;
        this.message = message;
    }


    public void setSuccess(Integer success) {
        this.success = success;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
