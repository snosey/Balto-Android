package com.example.snosey.balto.main.home_visit;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.login.RegistrationActivity;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import butterknife.ButterKnife;

/**
 * Created by Snosey on 2/12/2018.
 */

public class Second extends Fragment {

    Marker mCurrLocationMarker;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_visit_second, container, false);

        ButterKnife.inject(this, view);

        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.back)).setVisibility(View.VISIBLE);
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.menu)).setVisibility(View.GONE);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);


        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mGoogleMap) {
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker
                LatLng latLng = new LatLng(getArguments().getDouble("lat"), getArguments().getDouble("lng"));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);


                mGoogleMap.setMapStyle(new MapStyleOptions(WebService.MapStyle.mapStyle));
                //move map camera
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        });

        setData();

        return view;

    }

    private void setData() {
        UrlData urlData = new UrlData();
        urlData.add("type", RegistrationActivity.sharedPreferences.getString("lang","en"));
        urlData.add(WebService.HomeVisit.id_doctor_kind, WebService.HomeVisit.homeVisit);
        urlData.add(WebService.HomeVisit.id_main, getArguments().getString(WebService.HomeVisit.id_main));
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                recyclerView.setAdapter(new SecondAdapter(output));
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.HomeVisit.SubCategoryApi, urlData.get());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    private class SecondAdapter extends RecyclerView.Adapter<Second.MyViewHolder> {
        private JSONArray jsonArray;

        private SecondAdapter(String jsonResult) {
            try {
                JSONObject jsonObject = new JSONObject(jsonResult);
                jsonArray = jsonObject.getJSONArray("subCategory");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, final int position) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.home_visit_second_row, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final Second.MyViewHolder holder, final int position) {
            try {
                final JSONObject jsonObject = jsonArray.getJSONObject(position);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        Bundle bundle = new Bundle();
                        bundle.putString("json", jsonObject.toString());
                        bundle.putDouble("lat", getArguments().getDouble("lat"));
                        bundle.putDouble("lng", getArguments().getDouble("lng"));
                        bundle.putString("address", getArguments().getString("address"));
                        CategoryDetails fragment = new CategoryDetails();
                        FragmentTransaction ft = fm.beginTransaction();
                        fragment.setArguments(bundle);
                        ft.replace(R.id.fragment, fragment, "CategoryDetails");
                        ft.addToBackStack("CategoryDetails");
                        ft.commit();
                    }
                });
                holder.title.setText(jsonObject.getString("name"));
                Picasso.with(getContext()).load(WebService.Image.fullPathImage + jsonObject.getString("logo")).into(holder.logo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return (int) jsonArray.length();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public com.example.snosey.balto.Support.CustomTextView title;
        public ImageView logo;

        public MyViewHolder(View v) {
            super(v);
            logo = (ImageView) v.findViewById(R.id.icon);
            title = (com.example.snosey.balto.Support.CustomTextView) v.findViewById(R.id.title);
        }
    }
}
