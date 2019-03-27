package com.example.snosey.balto.main.reservations;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.WebService;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Snosey on 2/27/2018.
 */

public class ReservationsMain extends Fragment {
    @InjectView(R.id.tabs)
    TabLayout tabs;
    @InjectView(R.id.appbar)
    AppBarLayout appbar;
    @InjectView(R.id.viewpager)
    ViewPager viewpager;
    @InjectView(R.id.main_content)
    CoordinatorLayout mainContent;
    private ViewPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reservation_main, container, false);

        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.back)).setVisibility(View.GONE);
        ((RelativeLayout) getActivity().getWindow().getDecorView().findViewById(R.id.menuHome)).setVisibility(View.VISIBLE);
        ButterKnife.inject(this, view);

        viewpager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewpager);
        tabs = (TabLayout) view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewpager);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        ReservationComing reservationComing = new ReservationComing();
        if (getArguments() != null && getArguments().containsKey(WebService.Booking.receive_day)) {
            reservationComing.setArguments(getArguments());
        }
        adapter.addFragment(reservationComing, getActivity().getString(R.string.coming));
        adapter.addFragment(new ReservationPast(), getActivity().getString(R.string.past));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
