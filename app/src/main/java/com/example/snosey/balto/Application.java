package com.example.snosey.balto;

import android.support.v4.app.FragmentActivity;

import java.util.TimeZone;

/**
 * Created by Snosey on 2/27/2018.
 */

public class Application extends android.app.Application {
    public void onCreate() {
        super.onCreate();
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+2"));
    }

    private FragmentActivity mCurrentActivity = null;

    public FragmentActivity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(FragmentActivity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }
}
