package com.example.snosey.balto.login.create_account;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.snosey.balto.R;

/**
 * Created by Snosey on 1/31/2018.
 */

public class ProfilePic extends Fragment {
    ImageButton next;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_account_profile_pic, container, false);
        next=(ImageButton)view.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                NationalId fragment = new NationalId();
                ft.add(R.id.fragment, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        return view;
    }
}