package com.example.snosey.balto;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.main.ComingRequest;

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
            if (kind.equals(WebService.Notification.Types.bookingRequest)) {
                checkComingRequest(data);
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
}
