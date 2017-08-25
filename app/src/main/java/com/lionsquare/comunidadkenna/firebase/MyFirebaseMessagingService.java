package com.lionsquare.comunidadkenna.firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.lionsquare.comunidadkenna.task.NotificationComment;
import com.lionsquare.comunidadkenna.task.NotificationPetLost;

import org.json.JSONObject;

/**
 * Created by EDGAR ARANA on 01/08/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Log.e("FIREBASE", remoteMessage.getNotification().getBody());
        if (remoteMessage.getData().size() > 0) {

            try {
                Log.e("remoteMessage", remoteMessage.getData().toString());
                // TODO: 04/04/2017 data encapsula el cuerpo de json
                JSONObject jsonData = new JSONObject(remoteMessage.getData().toString());
                JSONObject json = jsonData.getJSONObject("data");
                switch (json.getInt("typeNotificatiion")) {
                    case 1:
                        NotificationPetLost notificationPetLost = new NotificationPetLost(getApplicationContext());
                        notificationPetLost.recivePet(json.getString("name_pet"), json.getInt("id"));
                        break;
                    case 2:
                        NotificationComment notificationComment = new NotificationComment(getApplicationContext());
                        notificationComment.reciveComment(json.getString("comment"), json.getInt("id_pet"));
                        break;
                }


            } catch (Exception e) {
                Log.e("error json parseo", "Exception: " + e.getMessage());
            }
        }
    }
}
