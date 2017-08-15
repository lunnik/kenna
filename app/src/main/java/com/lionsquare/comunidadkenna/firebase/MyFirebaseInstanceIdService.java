package com.lionsquare.comunidadkenna.firebase;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.lionsquare.comunidadkenna.activitys.MainActivity;
import com.lionsquare.comunidadkenna.utils.Preferences;


/**
 * Created by EDGAR ARANA on 01/08/2017.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIdService.class.getSimpleName();
    Context contextM;
    public static String sender = "629230285883";
    public String REGISTRATION_COMPLETE = "registrationComplete";
    private Preferences preferences;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        contextM = getApplicationContext();
        // Saving reg id to shared preferences

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);


        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {

        Intent intentSplah = new Intent(MainActivity.INTENT_FILTER_SPLAH);
        //intentSplah.putExtra("json", json);
        sendBroadcast(intentSplah);
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
        preferences = new Preferences(contextM);
        preferences.updateToken(token);

    }


}