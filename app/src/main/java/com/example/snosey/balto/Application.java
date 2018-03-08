package com.example.snosey.balto;

import android.support.v4.app.FragmentActivity;

/**
 * Created by Snosey on 2/27/2018.
 */

public class Application extends android.app.Application {
    public void onCreate() {
        super.onCreate();
    }

    private FragmentActivity mCurrentActivity = null;

    public FragmentActivity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(FragmentActivity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }
}
