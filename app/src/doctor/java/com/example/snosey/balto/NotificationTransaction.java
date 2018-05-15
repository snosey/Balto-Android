package com.example.snosey.balto;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.main.ComingRequest;
import com.example.snosey.balto.main.VideoCall;
import com.example.snosey.balto.main.reservations.ReservationsMain;

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
            Log.e("Kind Notification", kind);
            if (kind.equals(WebService.Notification.Types.bookingRequest)) {
                checkComingRequest(data);
            } else if (kind.equals(WebService.Notification.Types.newReservation)) {
                openComingRequest();
            } else if (kind.equals(WebService.Notification.Types.video_call)) {
                joinVideoRoom(data);
            } else if (kind.equals(WebService.Notification.Types.alarm) || kind.equals(WebService.Booking.bookingStateCancel)) {
                ReservationsAlarm(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void checkComingRequest(String data) {
        Bundle bundle = new Bundle();
        bundle.putString(WebService.Booking.id, data);
        FragmentManager fm = activity.getSupportFragmentManager();
        ComingRequest fragment = new ComingRequest();
        FragmentTransaction ft = fm.beginTransaction();
        fragment.setArguments(bundle);
        ft.replace(R.id.fragment, fragment, "ComingRequest");
        ft.addToBackStack("ComingRequest");
        ft.commit();
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
        ReservationsMain fragment = new ReservationsMain();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment, fragment, "ReservationsMain");
        ft.addToBackStack("ReservationsMain");
        ft.commit();
    }

    private void openComingRequest() {
        activity.recreate();
    }
}
