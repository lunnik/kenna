package com.lionsquare.comunidadkenna.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Awesome Pojo Generator
 */
public class CheckoutLogin {
    @SerializedName("type_account")
    @Expose
    private Integer type_account;
    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("message")
    @Expose
    private String message;

    public CheckoutLogin() {
    }

    public CheckoutLogin(Integer type_account, Integer success, String message) {
        this.type_account = type_account;
        this.success = success;
        this.message = message;
    }

    public void setType_account(Integer type_account) {
        this.type_account = type_account;
    }

    public Integer getType_account() {
        return type_account;
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