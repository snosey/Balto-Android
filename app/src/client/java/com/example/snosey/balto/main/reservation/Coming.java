package com.example.snosey.balto.main.reservation;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.snosey.balto.MainActivity;
import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.image.CircleTransform;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.login.RegistrationActivity;
import com.example.snosey.balto.main.DoctorProfile;
import com.example.snosey.balto.main.VideoCall;
import com.example.snosey.balto.main.home_visit.ProfissionLocation;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Snosey on 3/7/2018.
 */

public class Coming extends Fragment {


    JSONArray reservationJsonArray;
    ReservationAdapter reservationAdapter;
    RecyclerView recyclerViewReservation;
    @InjectView(R.id.day1)
    AppCompatButton day1;
    @InjectView(R.id.day2)
    AppCompatButton day2;
    @InjectView(R.id.day3)
    AppCompatButton day3;
    @InjectView(R.id.day4)
    AppCompatButton day4;
    @InjectView(R.id.day5)
    AppCompatButton day5;
    @InjectView(R.id.day6)
    AppCompatButton day6;
    @InjectView(R.id.day7)
    AppCompatButton day7;
    @InjectView(R.id.day1text)
    com.example.snosey.balto.Support.CustomTextView day1text;
    @InjectView(R.id.day2text)
    com.example.snosey.balto.Support.CustomTextView day2text;
    @InjectView(R.id.day3text)
    com.example.snosey.balto.Support.CustomTextView day3text;
    @InjectView(R.id.day4text)
    com.example.snosey.balto.Support.CustomTextView day4text;
    @InjectView(R.id.day5text)
    com.example.snosey.balto.Support.CustomTextView day5text;
    @InjectView(R.id.day6text)
    com.example.snosey.balto.Support.CustomTextView day6text;
    @InjectView(R.id.day7text)
    com.example.snosey.balto.Support.CustomTextView day7text;

    AppCompatButton dayClick;
    private Calendar currentDate;

    private List<JSONObject> arrayReservation = new ArrayList<>();

    @InjectView(R.id.date)
    com.example.snosey.balto.Support.CustomTextView date;
    @InjectView(R.id.dateLL)
    LinearLayout dateLL;
    @InjectView(R.id.calenderLL)
    LinearLayout calenderLL;

    @InjectView(R.id.all)
    Button all;

    String day = "";
    String month = "";
    String year = "";


    private Calendar nowPlus7 = Calendar.getInstance();
    private Calendar now = Calendar.getInstance();

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

        //setDate();


        nowPlus7.add(Calendar.DAY_OF_MONTH, 7);
        dateLL.setVisibility(View.GONE);
        calenderLL.setVisibility(View.VISIBLE);
        date.setText(getActivity().getString(R.string.filter));
        final Calendar cal = Calendar.getInstance();
        getComingReservation();
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        cal.set(year, monthOfYear, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy");
                        date.setText(sdf.format(cal.getTime()));
                        Coming.this.day = addZeroToString(dayOfMonth + "");
                        Coming.this.month = addZeroToString((monthOfYear + 1) + "");
                        Coming.this.year = addZeroToString(year + "");
                        getComingReservation();
                    }
                });
                all.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Coming.this.day = "";
                        Coming.this.month = "";
                        Coming.this.year = "";
                        getComingReservation();
                    }
                });
                dpd.setMaxDate(nowPlus7);
                dpd.setMinDate(now);
                dpd.show(getActivity().getFragmentManager(), "");

            }
        });


        return view;
    }

    private void getComingReservation() {
        UrlData urlData = new UrlData();
        try {
            urlData.add(WebService.Booking.id_client, MainActivity.jsonObject.getString("id"));
            urlData.add(WebService.Booking.lang, RegistrationActivity.sharedPreferences.getString("lang", "en"));
            urlData.add(WebService.Booking.type, WebService.Booking.client);
            if (!day.equals("")) {
                urlData.add(WebService.Booking.receive_day, day);
                urlData.add(WebService.Booking.receive_month, month);
                urlData.add(WebService.Booking.receive_year, year);
            }

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

        @SuppressLint("RestrictedApi")
        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            try {

                final JSONObject reservationObject = reservationJsonArray.getJSONObject(position);
                Calendar calendar = Calendar.getInstance();
                final long currentTimeMillis = TimeUnit.HOURS.toMillis(calendar.get(Calendar.HOUR_OF_DAY)) +
                        TimeUnit.DAYS.toMillis(calendar.get(Calendar.DAY_OF_MONTH)) + calendar.get(Calendar.DAY_OF_MONTH)
                        + TimeUnit.MINUTES.toMillis(calendar.get(Calendar.MINUTE));

                final long startTimeHour = TimeUnit.HOURS.toMillis(Long.parseLong(reservationObject.getString(WebService.Booking.receive_hour)));
                final long startTimeMin = TimeUnit.MINUTES.toMillis(Long.parseLong(reservationObject.getString(WebService.Booking.receive_minutes)));
                long duration = TimeUnit.MINUTES.toMillis(Long.parseLong(reservationObject.getString(WebService.Booking.duration)));
                final long startDay = TimeUnit.DAYS.toMillis(Long.parseLong(reservationObject.getString(WebService.Booking.receive_day))) +
                        Long.parseLong(reservationObject.getString(WebService.Booking.receive_month));

                long bookTotal = startTimeHour + startTimeMin + duration + startDay;


                holder.date.setText(reservationObject.getString(WebService.Booking.receive_hour) + ":" + reservationObject.getString(WebService.Booking.receive_minutes)
                        + " - " + reservationObject.getString(WebService.Booking.receive_day) + "/" + reservationObject.getString(WebService.Booking.receive_month) + "/" + reservationObject.getString(WebService.Booking.receive_year));

                Typeface font = Typeface.createFromAsset(
                        getActivity().getAssets(),
                        "fonts/arial.ttf");
                holder.firstName.setText(reservationObject.getString(WebService.Booking.firstName));
                holder.firstName.setTypeface(font, Typeface.BOLD);

                holder.logo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        try {
                            bundle.putString(WebService.HomeVisit.id_user, reservationObject.getString(WebService.Booking.id_doctor));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        DoctorProfile fragment = new DoctorProfile();
                        FragmentTransaction ft = fm.beginTransaction();
                        fragment.setArguments(bundle);
                        ft.replace(R.id.fragment, fragment, "DoctorProfile");
                        ft.addToBackStack("DoctorProfile");
                        ft.commit();
                    }
                });

                holder.type.setText(reservationObject.getString(WebService.Booking.subCategoryName));
                holder.kind.setText(reservationObject.getString(WebService.Booking.doctorKindName));
                holder.kind.setTypeface(font, Typeface.BOLD);
                holder.cancel.setTypeface(font, Typeface.BOLD);
                if (!reservationObject.getString(WebService.Booking.id_state).equals(WebService.Booking.bookingStateProcessing))
                    holder.cancel.setVisibility(View.GONE);
                else
                    holder.cancel.setVisibility(View.VISIBLE);
                holder.cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.HOUR, 1);
                        try {
                            final long currentTimeMillis = TimeUnit.HOURS.toMillis(calendar.get(Calendar.HOUR_OF_DAY)) + TimeUnit.MINUTES.toMillis(calendar.get(Calendar.MINUTE)) +
                                    TimeUnit.DAYS.toMillis(calendar.get(Calendar.DAY_OF_MONTH)) + calendar.get(Calendar.DAY_OF_MONTH);
                            final long startTimeHour = TimeUnit.HOURS.toMillis(Long.parseLong(reservationObject.getString(WebService.Booking.receive_hour)));
                            final long startTimeMin;
                            final long startDay = TimeUnit.DAYS.toMillis(Long.parseLong(reservationObject.getString(WebService.Booking.receive_day))) +
                                    Long.parseLong(reservationObject.getString(WebService.Booking.receive_month));
                            startTimeMin = TimeUnit.MINUTES.toMillis(Long.parseLong(reservationObject.getString(WebService.Booking.receive_minutes)));
                            if (addZeroToString(calendar.get(Calendar.DAY_OF_MONTH) + "").equals(reservationObject.getString(WebService.Booking.receive_day))) {
                                if (currentTimeMillis > startTimeHour + startTimeMin + startDay) {
                                    Toast.makeText(getContext(), getActivity().getString(R.string.cantCancel), Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setTitle("").setMessage(getActivity().getString(R.string.areYouSure)).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    if (reservationObject.getString(WebService.Booking.id_payment_way).equals(WebService.Booking.wallet)) {
                                        UrlData urlData2 = new UrlData();
                                        urlData2.add(WebService.Payment.id, reservationObject.getString(WebService.Booking.wallet_id));
                                        urlData2.add(WebService.Payment.state, WebService.Payment.refunded);
                                        new GetData(new GetData.AsyncResponse() {
                                            @Override
                                            public void processFinish(String output) throws JSONException {
                                                Log.e("walletOutput", output);
                                            }
                                        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Payment.updateWalletStateApi, urlData2.get());
                                    }
                                    updateBooking(reservationObject.getString("id"), WebService.Booking.bookingStatePatientCancel, reservationObject.getString(WebService.Booking.fcm_token));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                    }
                });

                if (holder.kind.getText().toString().equals(WebService.Booking.homeVisit)) {

                    holder.tracking.setVisibility(View.VISIBLE);
                    holder.tracking.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle bundle = new Bundle();
                            try {
                                bundle.putString(WebService.Booking.id, reservationObject.getString(WebService.Booking.id_doctor));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            ProfissionLocation fragment = new ProfissionLocation();
                            FragmentTransaction ft = fm.beginTransaction();
                            fragment.setArguments(bundle);
                            ft.replace(R.id.fragment, fragment, "ProfissionLocation");
                            ft.addToBackStack("ProfissionLocation");
                            ft.commit();
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
                    if (!reservationObject.getString(WebService.Booking.id_state).equals(WebService.Booking.bookingStateProcessing))
                        holder.cancel.setVisibility(View.GONE);
                    else
                        holder.cancel.setVisibility(View.VISIBLE);
                    holder.tracking.setVisibility(View.GONE);

                    if (currentTimeMillis < bookTotal - duration || reservationObject.getString(WebService.Booking.id_state).equals(WebService.Booking.bookingStateProcessing))
                        holder.call.setSupportImageTintList(getActivity().getResources().getColorStateList(R.color.silver));

                    if (currentTimeMillis >= bookTotal) {
                        if (reservationObject.getString(WebService.Booking.id_payment_way).equals(WebService.Booking.wallet)) {
                            {
                                UrlData urlData = new UrlData();
                                urlData.add(WebService.Payment.id, reservationObject.getString(WebService.Booking.wallet_id));
                                urlData.add(WebService.Payment.state, WebService.Payment.done);
                                new GetData(new GetData.AsyncResponse() {
                                    @Override
                                    public void processFinish(String output) throws JSONException {
                                    }
                                }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Payment.updateWalletStateApi, urlData.get());
                            }
                        }
                        Log.e("left:", currentTimeMillis + " / " + bookTotal);
                        moveToPast(reservationObject.getString(WebService.Booking.id_doctor),
                                reservationObject.getString(WebService.Booking.id_sub),
                                reservationObject.getString(WebService.Booking.wallet_id),
                                reservationObject.getString(WebService.Booking.id_payment_way),
                                reservationObject.getString(WebService.Booking.id),
                                reservationObject.getString(WebService.Booking.total_price),
                                reservationObject.getString(WebService.Booking.id_coupon_client),
                                reservationObject.getString(WebService.Booking.id_state));
                        return;
                    }

                    holder.call.setImageResource(R.drawable.video_call);
                    holder.call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                Calendar calendar = Calendar.getInstance();
                                final long currentTimeMillis = TimeUnit.HOURS.toMillis(calendar.get(Calendar.HOUR_OF_DAY)) + TimeUnit.MINUTES.toMillis(calendar.get(Calendar.MINUTE));
                                final long startTimeHour = TimeUnit.HOURS.toMillis(Long.parseLong(reservationObject.getString(WebService.Booking.receive_hour)));
                                final long startTimeMin = TimeUnit.MINUTES.toMillis(Long.parseLong(reservationObject.getString(WebService.Booking.receive_minutes)));
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


                            try {
                                checkVideoRoom();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        private void checkVideoRoom() throws JSONException {
                            UrlData urlData = new UrlData();
                            urlData.add(WebService.Booking.id_booking, reservationObject.getString(WebService.Booking.id));
                            new GetData(new GetData.AsyncResponse() {
                                @Override
                                public void processFinish(String output) throws JSONException {
                                    JSONObject jsonObject = new JSONObject(output).getJSONObject("booking");
                                    if (!jsonObject.getString(WebService.Booking.id_state).equals(WebService.Booking.bookingStateProcessing)) {
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

                                    } else {
                                        Toast.makeText(getActivity(), getActivity().getString(R.string.pleaseWaitDoctor), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Booking.getBookDataApi, urlData.get());
                        }

                    });
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

        public com.example.snosey.balto.Support.CustomTextView firstName, date, kind, type, cancel;
        public Button tracking;
        public AppCompatImageView logo, call;

        public MyViewHolder(View v) {
            super(v);
            type = (com.example.snosey.balto.Support.CustomTextView) v.findViewById(R.id.doctorType);
            firstName = (com.example.snosey.balto.Support.CustomTextView) v.findViewById(R.id.firstName);
            cancel = (com.example.snosey.balto.Support.CustomTextView) v.findViewById(R.id.cancel);
            date = (com.example.snosey.balto.Support.CustomTextView) v.findViewById(R.id.date);
            kind = (com.example.snosey.balto.Support.CustomTextView) v.findViewById(R.id.kind);
            tracking = (Button) v.findViewById(R.id.track);
            call = (AppCompatImageView) v.findViewById(R.id.call);
            logo = (AppCompatImageView) v.findViewById(R.id.logo);
        }
    }

    private void moveToPast(String idDoctor, String idSub, String walletId, String paymentId, String id, String totalPrice, String id_coupon_client, String idState) {
        if (false)
            return;
        UrlData urlData = new UrlData();
        urlData.add(WebService.Booking.id, id);
        if (idState.equals(WebService.Booking.bookingStateWorking)) {
            getPercntageDoctor(id, totalPrice, idSub
                    , idDoctor, paymentId);

            urlData.add(WebService.Booking.id_state, WebService.Booking.bookingStateDone);
            if (paymentId.equals(WebService.Booking.wallet)) {
                UrlData urlData2 = new UrlData();
                urlData2.add(WebService.Payment.id, walletId);
                urlData2.add(WebService.Payment.state, WebService.Payment.done);
                new GetData(new GetData.AsyncResponse() {
                    @Override
                    public void processFinish(String output) throws JSONException {
                    }
                }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Payment.updateWalletStateApi, urlData2.get());
            } else {
                checkIfCodeExist(totalPrice, id_coupon_client);
            }
        } else {
            if (paymentId.equals(WebService.Booking.wallet)) {
                UrlData urlData2 = new UrlData();
                urlData2.add(WebService.Payment.id, walletId);
                urlData2.add(WebService.Payment.state, WebService.Payment.refunded);
                new GetData(new GetData.AsyncResponse() {
                    @Override
                    public void processFinish(String output) throws JSONException {
                    }
                }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Payment.updateWalletStateApi, urlData2.get());
            }
            urlData.add(WebService.Booking.id_state, WebService.Booking.bookingStateTimeout);
        }

        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                getComingReservation();
            }
        }, getActivity(), false).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, WebService.Booking.updateBookingApi, urlData.get());
    }

    private void getPercntageDoctor(final String id, final String totalPrice, String idSub, final String idDoctor, final String paymentId) {
        UrlData urlData = new UrlData();
        urlData.add(WebService.Booking.id_sub, idSub);
        urlData.add(WebService.Booking.id_doctor_kind, WebService.homeVisit);
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                JSONObject jsonObject = new JSONObject(output);
                int intTotalPrice = Integer.parseInt(totalPrice);
                int adminPrice = (int) (intTotalPrice * Double.parseDouble(jsonObject.getString(WebService.Payment.online_percentage)) / 100);
                int doctorPrice = intTotalPrice - adminPrice;
                addPaymentToDB(intTotalPrice, adminPrice, doctorPrice, id, idDoctor, paymentId);
            }

            private void addPaymentToDB(int intTotalPrice, int adminPrice, int doctorPrice, String id, String idDoctor, String paymentId) {

                UrlData urlData = new UrlData();
                urlData.add(WebService.Payment.type, WebService.Payment.depet);
                urlData.add(WebService.Payment.total_money, intTotalPrice + "");
                urlData.add(WebService.Payment.doctor_money, "-" + doctorPrice);
                urlData.add(WebService.Payment.admin_money, adminPrice + "");
                urlData.add(WebService.Payment.payMob_id, "");
                urlData.add(WebService.Payment.id_user, idDoctor);
                urlData.add(WebService.Payment.id_payment_way, paymentId);
                urlData.add(WebService.Payment.id_booking, id);
                new GetData(new GetData.AsyncResponse() {
                    @Override
                    public void processFinish(final String output) throws JSONException {

                    }
                }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Payment.addPayment, urlData.get());


            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Payment.doctorPercentageMoneyApi, urlData.get());

    }


    @SuppressLint("RestrictedApi")
    @OnClick({R.id.day1, R.id.day2, R.id.day3, R.id.day4, R.id.day5, R.id.day6, R.id.day7})
    public void dayClick(View view) {
        setColorDefault();
        this.dayClick = ((AppCompatButton) view);
        this.dayClick.setTextColor(Color.WHITE);
        this.dayClick.setBackgroundResource(R.drawable.circel);
        this.dayClick.setSupportBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.red));

        currentDate = Calendar.getInstance();

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
        Calendar date = Calendar.getInstance();

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

    @SuppressLint("RestrictedApi")
    private void setDefaults(AppCompatButton defaults) {
        defaults.setSupportBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorPrimary));
        defaults.setTextColor(Color.WHITE);
        defaults.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
    }

    String addZeroToString(String s) {
        if (s.length() == 1)
            s = "0" + s;
        return s;
    }

    private void updateBooking(String id, String state, String regId) {
        sendNotification(regId, state);
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

    void sendNotification(String regId, String state) {
        final UrlData urlData = new UrlData();

        urlData.add(WebService.Notification.reg_id, regId);
        urlData.add(WebService.Notification.data, " ");
        urlData.add(WebService.Notification.kind, state);
        urlData.add(WebService.Notification.message, " ");
        urlData.add(WebService.Notification.title, "Reservation has been updated!");

        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {

            }
        }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Notification.notificationApi, urlData.get());

    }

    private void checkIfCodeExist(final String totalPrice, String id_coupon_client) {
        if (id_coupon_client.equals("0"))
            PAYNOW(totalPrice);
        else {
            UrlData urlData = new UrlData();
            urlData.add(WebService.PromoCode.id, id_coupon_client);
            new GetData(new GetData.AsyncResponse() {
                @Override
                public void processFinish(String output) throws JSONException {
                    JSONObject jsonObjectCode = new JSONObject(output).getJSONObject("coupon");
                    int price = Integer.parseInt(totalPrice) - (Integer.parseInt(totalPrice) * Integer.parseInt(jsonObjectCode.getString(WebService.PromoCode.discount))) / 100;
                    PAYNOW(price + "");

                }
            }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.PromoCode.promoCodeCheckApi, urlData.get());
        }
    }


    private void PAYNOW(final String latestPrice) {
        Log.e("latestPrice", latestPrice);
        String idClient = "";
        try {
            idClient = MainActivity.jsonObject.getString(WebService.Booking.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final RequestQueue MyRequestQueue = Volley.newRequestQueue(getActivity());
        UrlData urlData = new UrlData();
        urlData.add(WebService.Booking.id, idClient);
        urlData.add(WebService.Setting.default_location, "");
        final String finalIdClient = idClient;
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                JSONObject jsonObject = new JSONObject(output).getJSONObject("user");
                try {

                    Log.e("output", output);
                    if (!jsonObject.getString("payment_token").equals("null") || !jsonObject.getString("payment_token").equals("")) {
                        payByCredit(latestPrice, finalIdClient);
                    } else
                        Toast.makeText(getActivity(), "Error in payment!", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            private void payByCredit(final String latestPrice, final String idClient) {
                UrlData urlData1 = new UrlData();
                urlData1.add(WebService.Payment.user_id, idClient);
                urlData1.add(WebService.Payment.amount, latestPrice);
                urlData1.add(WebService.Payment.state, WebService.Payment.online);
                urlData1.add(WebService.Payment.direct, "true");
                StringRequest MyStringRequest = new StringRequest(Request.Method.POST, WebService.Payment.onlinePaymentApi + "?" + urlData1.get(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Payment Response", response);
                    }
                },
                        new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(getActivity(), getActivity().getString(R.string.error_null_cursor), Toast.LENGTH_SHORT).show();
                                //This code is executed if there is an error.
                            }
                        })

                {
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        HashMap<String, String> MyData = new HashMap<String, String>();
                     /*   MyData.put(WebService.Payment.user_id, idClient);
                        MyData.put(WebService.Payment.amount, latestPrice);
                        MyData.put(WebService.Payment.state, WebService.Payment.online);
                        MyData.put(WebService.Payment.direct, "true");*/
                        Log.e("MyData", MyData.toString());
                        return new JSONObject(MyData).toString().getBytes();
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }

                };
                MyStringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                MyRequestQueue.add(MyStringRequest);
            }
        }, getActivity(), false).
                executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Setting.updateUserApi, urlData.get());

    }

}
