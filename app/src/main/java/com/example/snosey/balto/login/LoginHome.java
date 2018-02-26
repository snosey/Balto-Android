package com.example.snosey.balto.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.snosey.balto.R;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Snosey on 1/31/2018.
 */

public class LoginHome extends Fragment {

    @InjectView(R.id.lang)
    TextView lang;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_home, container, false);
        ButterKnife.inject(this, view);

        if (Locale.getDefault().getLanguage().equals("ar")) {
            lang.setText(getActivity().getString(R.string.english));
        } else {
            lang.setText(getActivity().getString(R.string.arabic));
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
