package com.example.snosey.balto.login;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.snosey.balto.BuildConfig;
import com.example.snosey.balto.R;
import com.example.snosey.balto.login.create_account.Name;
import com.example.snosey.balto.login.create_account.SignUp;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Snosey on 1/31/2018.
 */

public class LoginHome extends Fragment {

    @InjectView(R.id.lang)
    com.example.snosey.balto.Support.CustomTextView lang;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_home, container, false);
        ButterKnife.inject(this, view);

        if (RegistrationActivity.sharedPreferences.getString("lang", "en").equals("ar")) {
            lang.setText(getActivity().getString(R.string.english));
        } else {
            lang.setText(getActivity().getString(R.string.arabic));
        }
        lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lang = ((com.example.snosey.balto.Support.CustomTextView) view).getText().toString();
                if (lang.equals(getString(R.string.arabic)))
                    lang = "ar";
                else
                    lang = "en";

                getActivity().recreate();
                Locale locale = new Locale(lang);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                onConfigurationChanged(config);


                SharedPreferences.Editor editor =  RegistrationActivity.sharedPreferences.edit();
                editor.putString("lang", lang);
                editor.commit();

            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.login)
    public void onLoginClicked() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        com.example.snosey.balto.login.SignIn fragment = new com.example.snosey.balto.login.SignIn();
        ft.add(R.id.fragment, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @OnClick(R.id.signup)
    public void onSignupClicked() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
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
}
