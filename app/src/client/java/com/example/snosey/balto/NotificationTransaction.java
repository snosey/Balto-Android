package com.example.snosey.balto;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.main.VideoCall;
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
            } else if (kind.equals(WebService.Notification.Types.video_call)) {
                joinVideoRoom(data);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void joinVideoRoom(String data) {
        Fragment myFragment = (Fragment) activity.getSupportFragmentManager().findFragmentByTag("VideoCall");
        if (!(myFragment == null || !myFragment.isVisible()))
            return;

        Log.e("data", data);
        FragmentManager fm = activity.getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString(WebService.Booking.id, data);
        VideoCall fragment = new VideoCall();
        FragmentTransaction ft = fm.beginTransaction();
        fragment.setArguments(bundle);
        ft.replace(R.id.fragment, fragment, "VideoCall");
        ft.addToBackStack("VideoCall");
        ft.commit();
    }

    private void ReservationsAlarm(String data) {

        Fragment myFragment = (Fragment) activity.getSupportFragmentManager().findFragmentByTag("ReservationsMain");
        if (!(myFragment == null || !myFragment.isVisible()))
            return;


        FragmentManager fm = activity.getSupportFragmentManager();
        Reservations fragment = new Reservations();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment, fragment, "ReservationsMain");
        ft.addToBackStack("ReservationsMain");
        ft.commit();
    }
}
