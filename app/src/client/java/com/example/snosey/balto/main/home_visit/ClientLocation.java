package com.example.snosey.balto.main.home_visit;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.WebService;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Snosey on 3/4/2018.
 */

public class ClientLocation extends Fragment {

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 99;
    GoogleMap mGoogleMap;
    List<Address> addresses;
    private Geocoder geocoder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_visit_client_location, container, false);
        ButterKnife.inject(this, view);

        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                setLocation(getArguments().getDouble("lat"), getArguments().getDouble("lng"));
            }
        });


        return view;
    }


    private void setLocation(double lat, double lng) {
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Place current location marker
        LatLng latLng = new LatLng(lat, lng);

        mGoogleMap.setMapStyle(new MapStyleOptions(WebService.MapStyle.mapStyle));
        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                try {
                    addresses = geocoder.getFromLocation(mGoogleMap.getCameraPosition().target.latitude, mGoogleMap.getCameraPosition().target.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.next)
    public void next() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Bundle bundle = new Bundle();

        try {
            Log.e("address", addresses.get(0).getAddressLine(0));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        bundle.putString("address", addresses.get(0).getAddressLine(0));
        bundle.putDouble("lat", mGoogleMap.getCameraPosition().target.latitude);
        bundle.putDouble("lng", mGoogleMap.getCameraPosition().target.longitude);
        bundle.putString(WebService.HomeVisit.id_user, getArguments().getString(WebService.HomeVisit.id_user));
        bundle.putString(WebService.HomeVisit.id_main, getArguments().getString(WebService.HomeVisit.id_main));
        Second fragment = new Second();
        FragmentTransaction ft = fm.beginTransaction();
        fragment.setArguments(bundle);
        ft.replace(R.id.fragment, fragment, "Second");
        ft.addToBackStack("Second");
        ft.commit();
    }

    @OnClick(R.id.search)
    public void search() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e("", "GooglePlayServicesRepairableException", e);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("", "GooglePlayServicesNotAvailableException", e);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                setLocation(PlacePicker.getPlace(data, getActivity()).getLatLng().latitude, PlacePicker.getPlace(data, getActivity()).getLatLng().longitude);
            }
        }
    }


}
