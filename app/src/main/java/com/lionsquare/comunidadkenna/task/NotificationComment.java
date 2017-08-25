package com.lionsquare.comunidadkenna.task;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.activitys.DetailsLostActivity;

/**
 * Created by EDGAR ARANA on 25/08/2017.
 */

public class NotificationComment {

    Context context;

    public NotificationComment(Context context) {
        this.context = context;

    }

    public void reciveComment(String title, int id) {

        Intent intent = new Intent(context, DetailsLostActivity.class);
        intent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //sonido
        Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //vibracion
        long[] vibracion = new long[]{1000, 500, 100};


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        //builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.devolucion));
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle(title);

        builder.setContentText("Se perdio cerca de ti ");
        //builder.setSubText(msj);
        builder.setSound(sonido);
        builder.setVibrate(vibracion);
        //usar led de notificacion
        builder.setLights(Color.WHITE, 1, 0);


        //Enviar la notificacion
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());



    }
}
