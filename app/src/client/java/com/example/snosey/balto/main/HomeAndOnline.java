package com.example.snosey.balto.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.WebService;

import butterknife.ButterKnife;
import butterknife.OnClick;
import com.example.snosey.balto.main.home_visit.Main;

/**
 * Created by Snosey on 2/12/2018.
 */

public class HomeAndOnline extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_main, container, false);


        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.back)).setVisibility(View.GONE);
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.menu)).setVisibility(View.VISIBLE);
        ((TextView) getActivity().getWindow().getDecorView().findViewById(R.id.title)).setText("");
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.right_icon)).setVisibility(View.GONE);

        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.onlineConsult)
    public void onOnlineConsultClicked() {
    }

    @OnClick(R.id.homeVisiting)
    public void onHomeVisitingClicked() {


        FragmentManager fm = getActivity().getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString(WebService.HomeVisit.id_user, getArguments().getString(WebService.HomeVisit.id_user));
        Main fragment = new Main();
        FragmentTransaction ft = fm.beginTransaction();
        fragment.setArguments(bundle);
        ft.replace(R.id.fragment, fragment, "Main");
        ft.addToBackStack("Main");
        ft.commit();
    }
}
