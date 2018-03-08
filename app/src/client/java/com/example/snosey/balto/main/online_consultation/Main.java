package com.example.snosey.balto.main.online_consultation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.snosey.balto.R;

/**
 * Created by Snosey on 3/8/2018.
 */

public class Main extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.online_consultation_main, container, false);

        return view;
    }
}
