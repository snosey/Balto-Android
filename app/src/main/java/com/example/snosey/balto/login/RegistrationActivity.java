package com.example.snosey.balto.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.BuildConfig;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.GetHashKey;
import com.example.snosey.balto.login.create_account.Name;
import com.example.snosey.balto.login.create_account.SignUp;

import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class RegistrationActivity extends FragmentActivity {
    SharedPreferences sharedPreferences;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/arial.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setContentView(R.layout.activity_registration);
        new GetHashKey(this);
        checkLang();
        {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            LoginHome fragment = new LoginHome();
            ft.replace(R.id.fragment, fragment);
            ft.commit();
        }

        if (com.example.snosey.balto.BuildConfig.APPLICATION_ID.contains("doctor")) {
            sharedPreferences = getSharedPreferences("login_doctor", Context.MODE_PRIVATE);
        } else {
            sharedPreferences = getSharedPreferences("login_client", Context.MODE_PRIVATE);
        }
        if (sharedPreferences.contains("type")) {
            Bundle bundle = new Bundle();
            bundle.putString("auto", "auto");
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            com.example.snosey.balto.login.SignIn fragment = new com.example.snosey.balto.login.SignIn();
            fragment.setArguments(bundle);
            ft.add(R.id.fragment, fragment);
            ft.addToBackStack("SignIn");
            ft.commit();
            ((ImageView) findViewById(R.id.background)).setVisibility(View.VISIBLE);
        }


    }

    public void signin(View view) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        com.example.snosey.balto.login.SignIn fragment = new com.example.snosey.balto.login.SignIn();
        ft.add(R.id.fragment, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void signup(View view) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (com.example.snosey.balto.BuildConfig.APPLICATION_ID.contains("doctor")) {
            Name fragment = new Name();
            ft.add(R.id.fragment, fragment);
        } else {
            SignUp fragment = new SignUp();
            ft.add(R.id.fragment, fragment);
        }
        ft.addToBackStack(null);
        ft.commit();
    }

    public void back(View view) {
        onBackPressed();
    }

    public void CreateAccount(View view) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Name fragment = new Name();
        ft.add(R.id.fragment, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void lang(View view) {
        String lang = ((TextView) view).getText().toString();
        if (lang.equals(getString(R.string.arabic)))
            lang = "ar";
        else
            lang = "en";

        recreate();
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        onConfigurationChanged(config);

        if (BuildConfig.APPLICATION_ID.contains("doctor"))
            sharedPreferences = getSharedPreferences("login_doctor", MODE_PRIVATE);
        else
            sharedPreferences = getSharedPreferences("login_client", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lang", lang);
        editor.commit();

    }


    void checkLang() {
        Locale locale;
        SharedPreferences sharedPreferences;

        if (com.example.snosey.balto.BuildConfig.APPLICATION_ID.contains("doctor"))
            sharedPreferences = getSharedPreferences("login_doctor", MODE_PRIVATE);
        else
            sharedPreferences = getSharedPreferences("login_client", MODE_PRIVATE);
        if (sharedPreferences.getString("lang", "en").equals("ar"))
            locale = new Locale("ar");
        else
            locale = new Locale("en");

        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        onConfigurationChanged(config);
    }

}
