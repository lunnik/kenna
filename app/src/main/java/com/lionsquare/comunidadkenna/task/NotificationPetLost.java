package com.lionsquare.comunidadkenna.task;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.NotificationCompat;

import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.activitys.DetailsLostActivity;
import com.lionsquare.comunidadkenna.activitys.MainActivity;

/**
 * Created by edgararana on 21/08/17.
 */

public class NotificationPetLost {
    Context context;

    public NotificationPetLost(Context context) {
        this.context = context;

    }


    public void recivePet(String title, int id) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(android.R.drawable.stat_sys_warning)
                        .setLargeIcon((((BitmapDrawable) context.getResources()
                                .getDrawable(R.mipmap.ic_launcher)).getBitmap()))
                        .setContentTitle(title)
                        .setContentText("Se perdio serca de donde estas")
                        .setContentInfo("4")
                        .setTicker("Se perdio serca de donde estas");


        Intent notIntent =
                new Intent(context, DetailsLostActivity.class);
        notIntent.putExtra("id", id);

        PendingIntent contIntent =
                PendingIntent.getActivity(
                        context, id, notIntent, 0);

        mBuilder.setContentIntent(contIntent);
    }


}
