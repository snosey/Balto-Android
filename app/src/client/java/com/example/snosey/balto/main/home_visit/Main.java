package com.example.snosey.balto.main.home_visit;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Snosey on 2/12/2018.
 */

public class Main extends Fragment implements OnMapReadyCallback {
    private static final int PLACE_PICKER_REQUEST = 3;
    @InjectView(R.id.doctorIcon)
    ImageView doctorIcon;
    @InjectView(R.id.doctorText)
    TextView doctorText;
    @InjectView(R.id.nurseIcon)
    ImageView nurseIcon;
    @InjectView(R.id.nurseText)
    TextView nurseText;
    @InjectView(R.id.nurseAidIcon)
    ImageView nurseAidIcon;
    @InjectView(R.id.nurseAidText)
    TextView nurseAidText;
    @InjectView(R.id.tabe3yIcon)
    ImageView tabe3yIcon;
    @InjectView(R.id.tabe3yText)
    TextView tabe3yText;
    private GoogleMap mGoogleMap;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private boolean mLocationPermissionGranted;
    LocationRequest mLocationRequest;
    FusedLocationProviderClient mFusedLocationClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    private boolean choosePlaceNow = true;
    private String categoryId;
    TextView title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_visit_main, container, false);

        ButterKnife.inject(this, view);

        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.back)).setVisibility(View.VISIBLE);
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.menu)).setVisibility(View.GONE);


        title = ((TextView) getActivity().getWindow().getDecorView().findViewById(R.id.title));
        title.setText(getActivity().getString(R.string.yourLocation));

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setData();
        return view;

    }

    private void setData() {
        UrlData urlData = new UrlData();
        urlData.add("type", Locale.getDefault().getLanguage());
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                try {
                    String imagePath = WebService.Image.fullPathImage;
                    final JSONObject jsonObject = new JSONObject(output);
                    Picasso.with(getActivity()).
                            load(imagePath + jsonObject.getJSONArray("mainCategory").getJSONObject(0).getString("logo")).
                            into(doctorIcon);

                    Picasso.with(getActivity()).
                            load(imagePath + jsonObject.getJSONArray("mainCategory").getJSONObject(1).getString("logo")).
                            into(nurseIcon);

                    Picasso.with(getActivity()).
                            load(imagePath + jsonObject.getJSONArray("mainCategory").getJSONObject(2).getString("logo")).
                            into(nurseAidIcon);

                    Picasso.with(getActivity()).
                            load(imagePath + jsonObject.getJSONArray("mainCategory").getJSONObject(3).getString("logo")).
                            into(tabe3yIcon);

                    doctorText.setText(jsonObject.getJSONArray("mainCategory").getJSONObject(0).getString("name"));
                    nurseText.setText(jsonObject.getJSONArray("mainCategory").getJSONObject(1).getString("name"));
                    nurseAidText.setText(jsonObject.getJSONArray("mainCategory").getJSONObject(2).getString("name"));
                    tabe3yText.setText(jsonObject.getJSONArray("mainCategory").getJSONObject(3).getString("name"));

                    doctorIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (choosePlaceNow) {
                                try {
                                    pickLocation(jsonObject.getJSONArray("mainCategory").getJSONObject(0).getString("id"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    nurseIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (choosePlaceNow) {
                                try {
                                    pickLocation(jsonObject.getJSONArray("mainCategory").getJSONObject(1).getString("id"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    nurseAidIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (choosePlaceNow) {
                                try {
                                    pickLocation(jsonObject.getJSONArray("mainCategory").getJSONObject(2).getString("id"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    tabe3yIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (choosePlaceNow) {
                                try {
                                    pickLocation(jsonObject.getJSONArray("mainCategory").getJSONObject(3).getString("id"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.HomeVisit.MainCatApi, urlData.get());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        //mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setMapStyle(new MapStyleOptions(WebService.MapStyle.mapStyle));

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                Log.e("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

                //move map camera
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        }

    };

    private void pickLocation(String i) {
        categoryId = i;
        choosePlaceNow = false;
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }


    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getContext())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                requestPermissions(
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            choosePlaceNow = true;
            if (resultCode == RESULT_OK) {
                String address = PlacePicker.getPlace(data, getActivity()).getAddress().toString();
                title.setText(address);

                FragmentManager fm = getActivity().getSupportFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putString("address", PlacePicker.getPlace(data, getActivity()).getAddress().toString());
                bundle.putDouble("lat", PlacePicker.getPlace(data, getActivity()).getLatLng().latitude);
                bundle.putDouble("lng", PlacePicker.getPlace(data, getActivity()).getLatLng().longitude);
                bundle.putString(WebService.HomeVisit.id_user, getArguments().getString(WebService.HomeVisit.id_user));
                bundle.putString(WebService.HomeVisit.id_main, categoryId + "");
                Second fragment = new Second();
                FragmentTransaction ft = fm.beginTransaction();
                fragment.setArguments(bundle);
                ft.replace(R.id.fragment, fragment, "Second");
                ft.addToBackStack("Second");
                ft.commit();
            }
        }
    }
}
