package com.lionsquare.comunidadkenna.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by EDGAR ARANA on 08/08/2017.
 */

public class ListLost {

    @SerializedName("list_lost")
    @Expose
    private List<PetLost> listLost = null;

    public List<PetLost> getListLost() {
        return listLost;
    }

    public void setListLost(List<PetLost> listLost) {
        this.listLost = listLost;
    }


    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("message")
    @Expose
    private String message;



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
