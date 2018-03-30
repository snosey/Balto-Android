package com.example.snosey.balto.Support.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.snosey.balto.R;
import com.example.snosey.balto.login.RegistrationActivity;

/**
 * Created by Snosey on 2/27/2018.
 */

public class NotifyService extends BroadcastReceiver {
    private static final int NOTIFICATION = 99;

    @Override
    public void onReceive(Context context, Intent intent) {
        {
            Log.e("Notification", ":alarm");
            Intent myIntent = new Intent(context, RegistrationActivity.class);
            String json = "{\"data\":\"" + intent.getStringExtra("data") +
                    "\",\"kind\":\"\"}";
            myIntent.putExtra("data", json);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, NOTIFICATION, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.logo_icon)
                            .setContentTitle(context.getString(R.string.reservation))
                            .setContentText(context.getString(R.string.alarmComingReservation))
                            .setContentIntent(pendingIntent);

            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION, mBuilder.build());
        }
    }
}
