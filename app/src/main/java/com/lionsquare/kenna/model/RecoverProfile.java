package com.lionsquare.kenna.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Awesome Pojo Generator
 * */
public class RecoverProfile{
  @SerializedName("success")
  @Expose
  private Integer success;
  @SerializedName("user")
  @Expose
  private User user;
  public void setSuccess(Integer success){
   this.success=success;
  }
  public Integer getSuccess(){
   return success;
  }
  public void setUser(User user){
   this.user=user;
  }
  public User getUser(){
   return user;
  }
}