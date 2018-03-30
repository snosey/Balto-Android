package com.example.snosey.balto.main.reservations;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snosey.balto.MainActivity;
import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.image.CircleTransform;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.main.MedicalReport;
import com.example.snosey.balto.main.Profile;
import com.example.snosey.balto.main.VideoCall;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Snosey on 3/7/2018.
 */

public class ReservationComing extends Fragment {


    JSONArray reservationJsonArray;
    ReservationAdapter reservationAdapter;
    RecyclerView recyclerViewReservation;
    @InjectView(R.id.day1)
    Button day1;
    @InjectView(R.id.day2)
    Button day2;
    @InjectView(R.id.day3)
    Button day3;
    @InjectView(R.id.day4)
    Button day4;
    @InjectView(R.id.day5)
    Button day5;
    @InjectView(R.id.day6)
    Button day6;
    @InjectView(R.id.day7)
    Button day7;
    @InjectView(R.id.day1text)
    TextView day1text;
    @InjectView(R.id.day2text)
    TextView day2text;
    @InjectView(R.id.day3text)
    TextView day3text;
    @InjectView(R.id.day4text)
    TextView day4text;
    @InjectView(R.id.day5text)
    TextView day5text;
    @InjectView(R.id.day6text)
    TextView day6text;
    @InjectView(R.id.day7text)
    TextView day7text;

    Button dayClick;
    private GregorianCalendar currentDate;

    String day;
    String month;
    String year;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reservation_list, container, false);

        ButterKnife.inject(this, view);

        reservationJsonArray = new JSONArray();
        reservationAdapter = new ReservationAdapter();
        recyclerViewReservation = (RecyclerView) view.findViewById(R.id.reservationRV);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewReservation.setLayoutManager(layoutManager);
        recyclerViewReservation.setAdapter(reservationAdapter);
        setDate();
        return view;
    }

    private void getComingReservation() {
        UrlData urlData = new UrlData();
        try {
            urlData.add(WebService.Booking.id_doctor, MainActivity.jsonObject.getString("id"));
            urlData.add(WebService.Booking.lang, Locale.getDefault().getLanguage());
            urlData.add(WebService.Booking.type, WebService.Booking.doctor);
            urlData.add(WebService.Booking.receive_day, day);
            urlData.add(WebService.Booking.receive_month, month);
            urlData.add(WebService.Booking.receive_year, year);

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    private class ReservationAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, final int position) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.reservation_row_coming, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            try {

                final JSONObject reservationObject = reservationJsonArray.getJSONObject(position);
                Calendar calendar = new GregorianCalendar();
                final long currentTimeMillis = TimeUnit.HOURS.toMillis(calendar.get(Calendar.HOUR_OF_DAY)) + TimeUnit.MINUTES.toMillis(calendar.get(Calendar.MINUTE));

                final long startTimeHour = TimeUnit.HOURS.toMillis(Long.parseLong(reservationObject.getString(WebService.Booking.receive_hour)));
                final long startTimeMin = TimeUnit.MINUTES.toMillis(Long.parseLong(reservationObject.getString(WebService.Booking.receive_minutes)));
                long duration = TimeUnit.MINUTES.toMillis(Long.parseLong(reservationObject.getString(WebService.Booking.duration)));
                long bookTotal = startTimeHour + startTimeMin + duration;


                holder.date.setText(reservationObject.getString(WebService.Booking.receive_hour) + ":" + reservationObject.getString(WebService.Booking.receive_minutes)
                        + " - " + reservationObject.getString(WebService.Booking.receive_day) + "/" + reservationObject.getString(WebService.Booking.receive_month) + "/" + reservationObject.getString(WebService.Booking.receive_year));

                Typeface font = Typeface.createFromAsset(
                        getActivity().getAssets(),
                        "fonts/arial.ttf");
                holder.firstName.setText(reservationObject.getString(WebService.Booking.firstName));
                holder.firstName.setTypeface(font, Typeface.BOLD);


                holder.kind.setText(reservationObject.getString(WebService.Booking.doctorKindName));
                holder.price.setText(reservationObject.getString(WebService.Booking.total_price) + " " + getActivity().getString(R.string.egp));
                holder.kind.setTypeface(font, Typeface.BOLD);


                if (holder.kind.getText().toString().equals(WebService.Booking.homeVisit)) {
                    holder.done.setVisibility(View.VISIBLE);
                    holder.done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle bundle = new Bundle();
                            try {
                                bundle.putString(WebService.Booking.id, reservationObject.getString(WebService.Booking.id));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            MedicalReport fragment = new MedicalReport();
                            FragmentTransaction ft = fm.beginTransaction();
                            fragment.setArguments(bundle);
                            ft.replace(R.id.fragment, fragment, "MedicalReport");
                            ft.addToBackStack("MedicalReport");
                            ft.commit();
                        }
                    });
                    holder.cancelOrStart.setText(getActivity().getString(R.string.start));
                    holder.cancelOrStart.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.colorPrimary));
                    holder.cancelOrStart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                                alertDialogBuilder.setMessage(R.string.areYouSure).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            updateBooking(reservationObject.getString(WebService.Booking.id), WebService.Booking.bookingStateStart);
                                            String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)",
                                                    reservationObject.getString(WebService.Booking.client_latitude),
                                                    reservationObject.getString(WebService.Booking.client_longitude), "");

                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                            intent.setPackage("com.google.android.apps.maps");
                                            startActivity(intent);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).show();

                            }
                        }
                    });
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
                    holder.done.setVisibility(View.GONE);
                    holder.cancelOrStart.setText(getActivity().getString(R.string.com_facebook_loginview_cancel_action));
                    holder.cancelOrStart.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.red));
                    holder.cancelOrStart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                updateBooking(reservationObject.getString(WebService.Booking.id), WebService.Booking.bookingStateCancel);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    if (currentTimeMillis >= bookTotal) {
                        Log.e("left:", currentTimeMillis + " / " + bookTotal);
                        updateBooking(reservationObject.getString(WebService.Booking.id), WebService.Booking.bookingStateDone);
                        return;
                    }

                    holder.call.setImageResource(R.drawable.video_call);
                    holder.call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Calendar calendar = new GregorianCalendar();
                            try {
                                final long currentTimeMillis = TimeUnit.HOURS.toMillis(calendar.get(Calendar.HOUR_OF_DAY)) + TimeUnit.MINUTES.toMillis(calendar.get(Calendar.MINUTE));
                                final long startTimeHour = TimeUnit.HOURS.toMillis(Long.parseLong(reservationObject.getString(WebService.Booking.receive_hour)));
                                final long startTimeMin;
                                startTimeMin = TimeUnit.MINUTES.toMillis(Long.parseLong(reservationObject.getString(WebService.Booking.receive_minutes)));
                                if (currentTimeMillis < startTimeHour + startTimeMin) {
                                    Toast.makeText(getContext(), getActivity().getString(R.string.waitToBookTime), Toast.LENGTH_LONG).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            if ((ContextCompat.checkSelfPermission(getActivity(),
                                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(getActivity(),
                                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {

                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA},
                                        55);
                                return;

                            }

                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            Bundle bundle = new Bundle();
                            try {
                                bundle.putString(WebService.Booking.id, reservationObject.getString(WebService.Booking.id));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            VideoCall fragment = new VideoCall();
                            FragmentTransaction ft = fm.beginTransaction();
                            fragment.setArguments(bundle);
                            ft.replace(R.id.fragment, fragment, "VideoCall");
                            ft.addToBackStack("VideoCall");
                            ft.commit();
                        }
                    });
                }

                if (!reservationObject.getString("image").equals("")) {
                    String imageLink = reservationObject.getString("image");
                    if (!imageLink.startsWith("https://"))
                        imageLink = WebService.Image.fullPathImage + imageLink;
                    Picasso.with(getActivity()).load(imageLink).transform(new CircleTransform()).into(holder.logo);
                }


                holder.logo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        try {
                            bundle.putString(WebService.HomeVisit.id_user, reservationObject.getString(WebService.Booking.id_client));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        Profile fragment = new Profile();
                        FragmentTransaction ft = fm.beginTransaction();
                        fragment.setArguments(bundle);
                        ft.replace(R.id.fragment, fragment, "Profile");
                        ft.addToBackStack("Profile");
                        ft.commit();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return (int) reservationJsonArray.length();
        }

    }

    private void updateBooking(String id, String state) {
        UrlData urlData = new UrlData();
        urlData.add(WebService.Booking.id, id);
        urlData.add(WebService.Booking.id_state, state);
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                getComingReservation();
            }
        }, getActivity(), false).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, WebService.Booking.updateBookingApi, urlData.get());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView firstName, date, kind, price;
        public Button cancelOrStart, done;
        public ImageView logo, call;

        public MyViewHolder(View v) {
            super(v);
            firstName = (TextView) v.findViewById(R.id.firstName);
            date = (TextView) v.findViewById(R.id.date);
            kind = (TextView) v.findViewById(R.id.kind);
            price = (TextView) v.findViewById(R.id.price);
            cancelOrStart = (Button) v.findViewById(R.id.cancelOrStart);
            done = (Button) v.findViewById(R.id.done);
            call = (ImageView) v.findViewById(R.id.call);
            logo = (ImageView) v.findViewById(R.id.logo);
        }
    }


    @OnClick({R.id.day1, R.id.day2, R.id.day3, R.id.day4, R.id.day5, R.id.day6, R.id.day7})
    public void dayClick(View view) {
        setColorDefault();
        this.dayClick = ((Button) view);
        this.dayClick.setTextColor(Color.WHITE);
        view.setBackgroundResource(R.drawable.circel);
        view.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.red));

        currentDate = new GregorianCalendar();

        switch (view.getId()) {
            case R.id.day1:
                currentDate.add(Calendar.DAY_OF_MONTH, 0);
                break;
            case R.id.day2:
                currentDate.add(Calendar.DAY_OF_MONTH, 1);
                break;
            case R.id.day3:
                currentDate.add(Calendar.DAY_OF_MONTH, 2);
                break;
            case R.id.day4:
                currentDate.add(Calendar.DAY_OF_MONTH, 3);
                break;
            case R.id.day5:
                currentDate.add(Calendar.DAY_OF_MONTH, 4);
                break;
            case R.id.day6:
                currentDate.add(Calendar.DAY_OF_MONTH, 5);
                break;
            case R.id.day7:
                currentDate.add(Calendar.DAY_OF_MONTH, 7);
                break;

        }

        day = addZeroToString(((currentDate.get(Calendar.DAY_OF_MONTH))) + "");
        month = addZeroToString(((currentDate.get(Calendar.MONTH)) + 1) + "");
        year = (currentDate.get(Calendar.YEAR)) + "";
        getComingReservation();
    }

    private void setColorDefault() {
        setDefaults(day1);
        setDefaults(day2);
        setDefaults(day3);
        setDefaults(day4);
        setDefaults(day5);
        setDefaults(day6);
        setDefaults(day7);
    }

    private void setDate() {
        Calendar date = new GregorianCalendar();

        day1.setText(date.get(Calendar.DAY_OF_MONTH) + "");
        day1text.setText(android.text.format.DateFormat.format("EEE", date));

        date.add(Calendar.DAY_OF_MONTH, +1);
        day2.setText(date.get(Calendar.DAY_OF_MONTH) + "");
        day2text.setText(android.text.format.DateFormat.format("EEE", date));

        date.add(Calendar.DAY_OF_MONTH, +1);
        day3.setText(date.get(Calendar.DAY_OF_MONTH) + "");
        day3text.setText(android.text.format.DateFormat.format("EEE", date));

        date.add(Calendar.DAY_OF_MONTH, +1);
        day4.setText(date.get(Calendar.DAY_OF_MONTH) + "");
        day4text.setText(android.text.format.DateFormat.format("EEE", date));

        date.add(Calendar.DAY_OF_MONTH, +1);
        day5.setText(date.get(Calendar.DAY_OF_MONTH) + "");
        day5text.setText(android.text.format.DateFormat.format("EEE", date));

        date.add(Calendar.DAY_OF_MONTH, +1);
        day6.setText(date.get(Calendar.DAY_OF_MONTH) + "");
        day6text.setText(android.text.format.DateFormat.format("EEE", date));

        date.add(Calendar.DAY_OF_MONTH, +1);
        day7.setText(date.get(Calendar.DAY_OF_MONTH) + "");
        day7text.setText(android.text.format.DateFormat.format("EEE", date));
        dayClick(day1);
    }

    private void setDefaults(Button defaults) {
        defaults.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.colorPrimary));
        defaults.setTextColor(Color.WHITE);
        defaults.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
    }

    String addZeroToString(String s) {
        if (s.length() == 1)
            s = "0" + s;
        return s;
    }

}