package com.example.snosey.balto;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.firebase.FirebaseApp;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Snosey on 2/27/2018.
 */

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("firebase", "true");
        FirebaseApp.initializeApp(this);
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+02"));
        Log.e("current time=", Calendar.getInstance().getTime().toString());
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    private FragmentActivity mCurrentActivity = null;

    public FragmentActivity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(FragmentActivity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }
}
