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
        if (intent.getBooleanExtra("now", true))
            notificationUtils.showNotificationMessage(context.getString(R.string.reservation), context.getString(R.string.alarmComingReservationNow), Calendar.getInstance().getTime().toString(), myIntent);
        else
            notificationUtils.showNotificationMessage(context.getString(R.string.reservation), context.getString(R.string.alarmComingReservation), Calendar.getInstance().getTime().toString(), myIntent);

    }
}
