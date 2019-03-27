package com.example.snosey.balto;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.example.snosey.balto.Support.notification.NotifyService;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.main.ChatRoom;
import com.example.snosey.balto.main.ComingRequest;
import com.example.snosey.balto.main.VideoCall;
import com.example.snosey.balto.main.reservations.ReservationsMain;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

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
            } else if (kind.equals(WebService.Notification.Types.bookingRequestOnline)) {
                saveAlarm(data);
            } else if (kind.equals(WebService.Notification.Types.newReservation)) {
                openComingRequest("7");
            } else if (kind.equals(WebService.Notification.Types.video_call)) {
                joinVideoRoom(data);
            } else if (kind.equals(WebService.Notification.Types.alarm) || kind.equals(WebService.Booking.bookingStatePatientCancel)) {
                ReservationsAlarm(data);
            } else if (kind.equals(WebService.Notification.Types.newMsg)) {
                openChatRoom(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveAlarm(String id) {
        UrlData urlData = new UrlData();
        urlData.add(WebService.Booking.id_booking, id);
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                JSONObject booking = new JSONObject(output).getJSONObject("booking");
                int receive_hour = Integer.parseInt(booking.getString(WebService.Booking.receive_hour));
                int receive_minutes = Integer.parseInt(booking.getString(WebService.Booking.receive_minutes));
                int receive_day = Integer.parseInt(booking.getString(WebService.Booking.receive_day));
                int receive_month = Integer.parseInt(booking.getString(WebService.Booking.receive_month));
                int receive_year = Integer.parseInt(booking.getString(WebService.Booking.receive_year));

                saveAlarmNow(receive_year, receive_month, receive_day, receive_hour, receive_minutes, Integer.parseInt(booking.getString(WebService.Booking.id)));
                saveAlarm15Min(receive_year, receive_month, receive_day, receive_hour, receive_minutes, Integer.parseInt(booking.getString(WebService.Booking.id) + 15));

            }
        }, activity, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Booking.getBookDataApi, urlData.get());

    }

    private void openChatRoom(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            String chatId = jsonObject.getString("id_chat");
            FragmentManager fm = activity.getSupportFragmentManager();
            Bundle bundle = new Bundle();
            bundle.putString("id", chatId);
            ChatRoom fragment = new ChatRoom();
            FragmentTransaction ft = fm.beginTransaction();
            fragment.setArguments(bundle);
            ft.replace(R.id.fragment, fragment, "ChatRoom");
            ft.addToBackStack("Chat");
            ft.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void saveAlarm15Min(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minute, int bookingId) {


        Calendar now = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
        calendar.add(Calendar.MINUTE, -15);
        calendar.add(Calendar.MONTH, -1);

        if (now.getTimeInMillis() > calendar.getTimeInMillis())
            return;

        Log.e("Save Alarm", calendar.getTime().toString());

        ComponentName receiver = new ComponentName(activity, NotifyService.class);
        PackageManager pm = activity.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent1 = new Intent(activity, NotifyService.class);
        intent1.putExtra("now", false);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity,
                bookingId, intent1,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                0, pendingIntent);


    }

    private void saveAlarmNow(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minute, int bookingId) {


        Calendar now = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
        calendar.add(Calendar.MONTH, -1);
        calendar.add(Calendar.MINUTE, -1);

        if (now.getTimeInMillis() > calendar.getTimeInMillis())
            return;

        Log.e("Save Alarm", calendar.getTime().toString());

        ComponentName receiver = new ComponentName(activity, NotifyService.class);
        PackageManager pm = activity.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent1 = new Intent(activity, NotifyService.class);
        intent1.putExtra("now", true);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity,
                bookingId, intent1,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                0, pendingIntent);


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

    private void openComingRequest(String day) {
        Fragment myFragment = (Fragment) activity.getSupportFragmentManager().findFragmentByTag("ReservationsMain");
        if (!(myFragment == null || !myFragment.isVisible()))
            return;

        Bundle bundle = new Bundle();
        bundle.putString(WebService.Booking.receive_day, day);
        FragmentManager fm = activity.getSupportFragmentManager();
        ReservationsMain fragment = new ReservationsMain();
        fragment.setArguments(bundle);
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment, fragment, "ReservationsMain");
        ft.addToBackStack("ReservationsMain");
        ft.commit();
    }
}
