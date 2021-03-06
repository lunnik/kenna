package com.lionsquare.comunidadkenna.firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

/**
 * Created by EDGAR ARANA on 01/08/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("FIREBASE", remoteMessage.getNotification().getBody());
        if (remoteMessage.getData().size() > 0) {

            try {

                Log.e("remoteMessage", remoteMessage.getData().toString());
                // TODO: 04/04/2017 data encapsula el cuerpo de json
                JSONObject jsonData = new JSONObject(remoteMessage.getData().toString());
                JSONObject json = jsonData.getJSONObject("data");

            } catch (Exception e) {
                Log.e("error json parseo", "Exception: " + e.getMessage());
            }
        }
    }
}
