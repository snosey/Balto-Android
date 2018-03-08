package com.example.snosey.balto;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.main.reservation.Reservations;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Snosey on 2/26/2018.
 */

public class NotificationTransaction {
    FragmentActivity activity;

    public NotificationTransaction(FragmentActivity activity, String jsonString) {
        this.activity = activity;
        try {
            JSONObject notification = new JSONObject(jsonString);
            String kind = notification.getString("kind");
            String data = notification.getString("data");
            if (kind.equals(WebService.Notification.Types.alarm)) {
                ReservationsAlarm(data);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void ReservationsAlarm(String data) {
        FragmentManager fm = activity.getSupportFragmentManager();
        Reservations fragment = new Reservations();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment, fragment, "ReservationsMain");
        ft.addToBackStack("ReservationsMain");
        ft.commit();
    }
}
