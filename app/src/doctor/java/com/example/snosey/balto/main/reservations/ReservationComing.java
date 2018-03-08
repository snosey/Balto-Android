package com.example.snosey.balto.main.reservations;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.snosey.balto.MainActivity;
import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.image.CircleTransform;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by Snosey on 3/7/2018.
 */

public class ReservationComing extends android.support.v4.app.Fragment {


    JSONArray reservationJsonArray;
    ReservationAdapter reservationAdapter;
    RecyclerView recyclerViewReservation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reservation_list, container, false);


        reservationJsonArray = new JSONArray();
        reservationAdapter = new ReservationAdapter();
        recyclerViewReservation = (RecyclerView) view.findViewById(R.id.reservationRV);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewReservation.setLayoutManager(layoutManager);
        recyclerViewReservation.setAdapter(reservationAdapter);

        getComingReservation();
        return view;
    }

    private void getComingReservation() {
        UrlData urlData = new UrlData();
        try {
            urlData.add(WebService.Booking.id_doctor, MainActivity.jsonObject.getString("id"));
            urlData.add(WebService.Booking.lang, Locale.getDefault().getLanguage());
            urlData.add(WebService.Booking.type, WebService.Booking.doctor);
            urlData.add(WebService.Booking.state, WebService.Booking.coming);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                try {
                    reservationJsonArray = new JSONObject(output).getJSONArray("booking");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                reservationAdapter.notifyDataSetChanged();
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Booking.reservationsApi, urlData.get());
    }

    private class ReservationAdapter extends RecyclerView.Adapter<ReservationComing.MyViewHolder> {

        @Override
        public ReservationComing.MyViewHolder onCreateViewHolder(ViewGroup parent, final int position) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.reservation_row_coming, parent, false);
            return new ReservationComing.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ReservationComing.MyViewHolder holder, final int position) {
            try {
                final JSONObject reservationObject = reservationJsonArray.getJSONObject(position);
                holder.date.setText(reservationObject.getString(WebService.Booking.receive_hour) + ":" + reservationObject.getString(WebService.Booking.receive_minutes)
                        + " - " + reservationObject.getString(WebService.Booking.receive_day) + "/" + reservationObject.getString(WebService.Booking.receive_month) + "/" + reservationObject.getString(WebService.Booking.receive_year));

                holder.firstName.setText(reservationObject.getString(WebService.Booking.firstName));
                holder.firstName.setTypeface(null, Typeface.BOLD);

                holder.kind.setText(reservationObject.getString(WebService.Booking.doctorKindName));
                holder.kind.setTypeface(null, Typeface.BOLD);
                holder.details.setTypeface(null, Typeface.BOLD);

                if (holder.kind.getText().toString().equals(WebService.Booking.homeVisit)) {
                    holder.call.setImageResource(R.drawable.phone);
                    holder.call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = null;
                            try {
                                intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + reservationObject.getString(WebService.Booking.phone)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);
                                return;
                            }
                            startActivity(intent);

                        }
                    });
                } else {
                    holder.call.setImageResource(R.drawable.video_call);
                }

                if (!reservationObject.getString("image").equals("")) {
                    String imageLink = reservationObject.getString("image");
                    if (!imageLink.startsWith("https://"))
                        imageLink = WebService.Image.fullPathImage + imageLink;
                    Picasso.with(getActivity()).load(imageLink).transform(new CircleTransform()).into(holder.logo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return (int) reservationJsonArray.length();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView firstName, date, kind;
        public Button details;
        public ImageView logo, call;

        public MyViewHolder(View v) {
            super(v);
            firstName = (TextView) v.findViewById(R.id.firstName);
            date = (TextView) v.findViewById(R.id.date);
            kind = (TextView) v.findViewById(R.id.kind);
            details = (Button) v.findViewById(R.id.details);
            call = (ImageView) v.findViewById(R.id.call);
            logo = (ImageView) v.findViewById(R.id.logo);
        }
    }

}
