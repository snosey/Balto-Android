package com.example.snosey.balto.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.snosey.balto.R;

/**
 * Created by Snosey on 1/31/2018.
 */

public class SignUp extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_up, container, false);
        return view;
    }
}
