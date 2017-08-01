package com.lionsquare.comunidadkenna.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Awesome Pojo Generator
 * */
public class Register{
  @SerializedName("success")
  @Expose
  private Integer success;
  @SerializedName("message")
  @Expose
  private String message;
  public void setSuccess(Integer success){
   this.success=success;
  }
  public Integer getSuccess(){
   return success;
  }
  public void setMessage(String message){
   this.message=message;
  }
  public String getMessage(){
   return message;
  }
}