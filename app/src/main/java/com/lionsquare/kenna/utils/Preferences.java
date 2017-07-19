package com.lionsquare.kenna.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by EDGAR ARANA on 18/07/2017.
 */

public class Preferences {

    private Context context;

    public Preferences(Context context) {
        this.context = context;
    }


    public boolean setProfil(String token, String name, String email, String url, String cover, int typeLogin, boolean flag) {
        try {
            SharedPreferences sessionUser = context.getSharedPreferences("auth_Session", MODE_PRIVATE);
            SharedPreferences.Editor editor = sessionUser.edit();
            editor.putString("token", token);
            editor.putString("name", name);
            editor.putString("email", email);
            editor.putString("url", url);
            editor.putString("cover", cover);
            editor.putInt("typeLogin", typeLogin);
            editor.putBoolean("flag", flag);
            editor.apply();
            return true;
        } catch (Exception e) {
            return false;
        }

    }


    public boolean closeProfile() {
        try {
            SharedPreferences sessionUser = context.getSharedPreferences("auth_Session", MODE_PRIVATE);
            SharedPreferences.Editor editor = sessionUser.edit();
            editor.putString("token", "");
            editor.putString("name", "");
            editor.putString("email", "");
            editor.putString("url", "");
            editor.putString("cover", "");
            editor.putInt("typeLogin", 0);
            editor.putBoolean("flag", false);
            editor.apply();
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public String getName() {
        SharedPreferences recuperarToken = context.getSharedPreferences("auth_Session", MODE_PRIVATE);
        return recuperarToken.getString("name", "");
    }

    public String getEmail() {
        SharedPreferences recuperarToken = context.getSharedPreferences("auth_Session", MODE_PRIVATE);
        return recuperarToken.getString("email", "");
    }

    public boolean getFlag() {
        SharedPreferences recuperarToken = context.getSharedPreferences("auth_Session", MODE_PRIVATE);
        return recuperarToken.getBoolean("flag", false);
    }

    public String getImagePerfil() {
        SharedPreferences recuperarToken = context.getSharedPreferences("auth_Session", MODE_PRIVATE);
        return recuperarToken.getString("url", "");
    }

    public String getCover() {
        SharedPreferences recuperarToken = context.getSharedPreferences("auth_Session", MODE_PRIVATE);
        return recuperarToken.getString("cover", "");
    }

    public int getTypeLogin() {
        SharedPreferences recuperarToken = context.getSharedPreferences("auth_Session", MODE_PRIVATE);
        return recuperarToken.getInt("typeLogin", 0);
    }


}
