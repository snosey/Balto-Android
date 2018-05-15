package com.example.snosey.balto.Support.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.login.RegistrationActivity;

import java.util.Calendar;

/**
 * Created by Snosey on 2/27/2018.
 */

public class NotifyService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationUtils notificationUtils = new NotificationUtils(context);

        Intent myIntent = new Intent(context, RegistrationActivity.class);
        String json = "{\"data\":\"" +
                "\",\"kind\":\"" + WebService.Notification.Types.alarm + "\"}";
        myIntent.putExtra("data", json);
        notificationUtils.showNotificationMessage(context.getString(R.string.reservation), context.getString(R.string.alarmComingReservation), Calendar.getInstance().getTime().toString(), myIntent);


        /*{

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
        }*/
    }
}
