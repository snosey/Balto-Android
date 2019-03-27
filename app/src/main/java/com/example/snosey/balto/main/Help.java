package com.example.snosey.balto.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.snosey.balto.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Snosey on 5/1/2018.
 */

public class Help extends Fragment {

    @InjectView(R.id.call)
    AppCompatImageView call;
    @InjectView(R.id.mail)
    AppCompatImageView mail;
    @InjectView(R.id.web)
    AppCompatImageView web;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.help, container, false);
        ButterKnife.inject(this, view);

        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.back)).setVisibility(View.VISIBLE);
        ((RelativeLayout) getActivity().getWindow().getDecorView().findViewById(R.id.menuHome)).setVisibility(View.GONE);


        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "01201146214"));
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);
                    return;
                }
                startActivity(intent);
            }
        });

        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "Info@elbalto.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "مساعده");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.elbalto.com/"));
                startActivity(browserIntent);
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}

