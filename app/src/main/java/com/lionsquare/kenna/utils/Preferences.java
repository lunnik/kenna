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


    public boolean setProfil(String token_social, String name, String email, String url, String cover, int typeLogin, boolean flag, String token) {
        try {
            SharedPreferences sessionUser = context.getSharedPreferences("auth_Session", MODE_PRIVATE);
            SharedPreferences.Editor editor = sessionUser.edit();
            editor.putString("token_social", token_social);
            editor.putString("name", name);
            editor.putString("email", email);
            editor.putString("url", url);
            editor.putString("cover", cover);
            editor.putInt("typeLogin", typeLogin);
            editor.putBoolean("flag", flag);
            editor.putString("token", token);
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
            editor.clear();
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

    public String getTokenSosial() {
        SharedPreferences recuperarToken = context.getSharedPreferences("auth_Session", MODE_PRIVATE);
        return recuperarToken.getString("token_social", "");
    }

    public int getTypeLogin() {
        SharedPreferences recuperarToken = context.getSharedPreferences("auth_Session", MODE_PRIVATE);
        return recuperarToken.getInt("typeLogin", 0);
    }

    public String getToken() {
        SharedPreferences recuperarToken = context.getSharedPreferences("auth_Session", MODE_PRIVATE);
        return recuperarToken.getString("token", "");
    }


}
