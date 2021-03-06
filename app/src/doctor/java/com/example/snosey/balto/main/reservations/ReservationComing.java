package com.example.snosey.balto.main.reservations;

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
import com.example.snosey.balto.main.ClientProfile;
import com.example.snosey.balto.main.MedicalReport;
import com.example.snosey.balto.main.RateDialog;
import com.example.snosey.balto.main.VideoCall;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
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
        // setDate();

        if (getArguments() != null && getArguments().containsKey(WebService.Booking.receive_day)) {
            dayClick(day2);
        }

        nowPlus7.add(Calendar.DAY_OF_MONTH, 7);
        dateLL.setVisibility(View.GONE);
        calenderLL.setVisibility(View.VISIBLE);
        date.setText(getActivity().getString(R.string.filter));
        final Calendar cal = Calendar.getInstance();
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        cal.set(year, monthOfYear, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy");
                        date.setText(sdf.format(cal.getTime()));
                        ReservationComing.this.day = addZeroToString(dayOfMonth + "");
                        ReservationComing.this.month = addZeroToString((monthOfYear + 1) + "");
                        ReservationComing.this.year = addZeroToString(year + "");
                        getComingReservation();
                    }
                });
                all.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ReservationComing.this.day = "";
                        ReservationComing.this.month = "";
                        ReservationComing.this.year = "";
                        getComingReservation();
                    }
                });

                dpd.setMaxDate(nowPlus7);
                dpd.setMinDate(now);
                dpd.show(getActivity().getFragmentManager(), "");

            }
        });

        getComingReservation();
        return view;
    }

    private void getComingReservation() {
        UrlData urlData = new UrlData();
        try {
            urlData.add(WebService.Booking.id_doctor, MainActivity.jsonObject.getString("id"));
            urlData.add(WebService.Booking.lang, RegistrationActivity.sharedPreferences.getString("lang", "en"));
            urlData.add(WebService.Booking.type, WebService.Booking.doctor);

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
                final long duration = TimeUnit.MINUTES.toMillis(Long.parseLong(reservationObject.getString(WebService.Booking.duration)));
                final long startDay = TimeUnit.DAYS.toMillis(Long.parseLong(reservationObject.getString(WebService.Booking.receive_day))) +
                        Long.parseLong(reservationObject.getString(WebService.Booking.receive_month));
                final long bookTotal = startTimeHour + startTimeMin + duration + startDay;


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


                if (reservationObject.getString(WebService.Booking.id_doctor_kind).equals(WebService.homeVisit)) {
                    holder.done.setVisibility(View.VISIBLE);
                    holder.cancelOrStart.setVisibility(View.VISIBLE);
                    holder.arrive.setVisibility(View.VISIBLE);
                    holder.arrive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                                alertDialogBuilder.setMessage(R.string.areYouSure).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            updateBooking(reservationObject.getString(WebService.Booking.wallet_id), reservationObject.getString(WebService.Booking.id_payment_way), reservationObject.getString(WebService.Booking.fcm_token), reservationObject.getString(WebService.Booking.id), WebService.Booking.bookingStateWorking, reservationObject.getString(WebService.Booking.id_client), getActivity().getString(R.string.doctorArrive), reservationObject.getString(WebService.Booking.id_state));
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
                    holder.done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                                alertDialogBuilder.setMessage(R.string.areYouSure).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            getPercntageDoctor("", reservationObject.getString(WebService.Booking.id), reservationObject.getString(WebService.Booking.total_price),
                                                    reservationObject.getString(WebService.Booking.id_sub), reservationObject.getString(WebService.Booking.id_client), reservationObject.getString(WebService.Booking.id_payment_way));
                                            updateBooking(reservationObject.getString(WebService.Booking.wallet_id),
                                                    reservationObject.getString(WebService.Booking.id_payment_way), reservationObject.getString(WebService.Booking.fcm_token),
                                                    reservationObject.getString(WebService.Booking.id), WebService.Booking.bookingStateDone, reservationObject.getString(WebService.Booking.id_client)
                                                    , getActivity().getString(R.string.doctorFinished), reservationObject.getString(WebService.Booking.id_state));
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
                    holder.cancelOrStart.setText(getActivity().getString(R.string.start));
                    holder.cancelOrStart.setSupportBackgroundTintList(getActivity().getResources().getColorStateList(R.color.colorPrimary));
                    holder.cancelOrStart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                                alertDialogBuilder.setMessage(R.string.areYouSure).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            updateBooking(reservationObject.getString(WebService.Booking.wallet_id), reservationObject.getString(WebService.Booking.id_payment_way),
                                                    reservationObject.getString(WebService.Booking.fcm_token), reservationObject.getString(WebService.Booking.id),
                                                    WebService.Booking.bookingStateStart, reservationObject.getString(WebService.Booking.id_client), getActivity().getString(R.string.doctorStart),
                                                    reservationObject.getString(WebService.Booking.id_state));

                                            String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)",
                                                    Double.parseDouble(reservationObject.getString(WebService.Booking.client_latitude))
                                                    , Double.parseDouble(reservationObject.getString(WebService.Booking.client_longitude)), "");

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

                    holder.arrive.setVisibility(View.GONE);
                    holder.done.setVisibility(View.GONE);
                    holder.cancelOrStart.setText(getActivity().getString(R.string.com_facebook_loginview_cancel_action));

                    if (!reservationObject.getString(WebService.Booking.id_state).equals(WebService.Booking.bookingStateProcessing))
                        holder.cancelOrStart.setVisibility(View.GONE);
                    else
                        holder.cancelOrStart.setVisibility(View.VISIBLE);

                    holder.cancelOrStart.setSupportBackgroundTintList(getActivity().getResources().getColorStateList(R.color.red));
                    holder.cancelOrStart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.HOUR, 1);
                            try {
                                final long currentTimeMillis = TimeUnit.HOURS.toMillis(calendar.get(Calendar.HOUR_OF_DAY)) + TimeUnit.MINUTES.toMillis(calendar.get(Calendar.MINUTE));
                                final long startTimeHour = TimeUnit.HOURS.toMillis(Long.parseLong(reservationObject.getString(WebService.Booking.receive_hour)));
                                final long startTimeMin;
                                startTimeMin = TimeUnit.MINUTES.toMillis(Long.parseLong(reservationObject.getString(WebService.Booking.receive_minutes)));
                                if (addZeroToString(calendar.get(Calendar.DAY_OF_MONTH) + "").equals(reservationObject.getString(WebService.Booking.receive_day))) {
                                    if (currentTimeMillis > startTimeHour + startTimeMin) {
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
                                        updateBooking(reservationObject.getString(WebService.Booking.wallet_id),
                                                reservationObject.getString(WebService.Booking.id_payment_way),
                                                reservationObject.getString(WebService.Booking.fcm_token),
                                                reservationObject.getString(WebService.Booking.id),
                                                WebService.Booking.bookingStateDoctorCancel,
                                                reservationObject.getString(WebService.Booking.id_client),
                                                getActivity().getString(R.string.reservationCancel),
                                                reservationObject.getString(WebService.Booking.id_state));
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

                    if (currentTimeMillis >= bookTotal) {
                        Log.e("left:", currentTimeMillis + " / " + bookTotal);
                        if (reservationObject.getString(WebService.Booking.id_state).equals(WebService.Booking.bookingStateProcessing))
                            updateBooking(reservationObject.getString(WebService.Booking.wallet_id), reservationObject.getString(WebService.Booking.id_payment_way), reservationObject.getString(WebService.Booking.fcm_token), reservationObject.getString(WebService.Booking.id), WebService.Booking.bookingStateTimeout, reservationObject.getString(WebService.Booking.id_client), getActivity().getString(R.string.doctorFinished), reservationObject.getString(WebService.Booking.id_state));
                        else
                            updateBooking(reservationObject.getString(WebService.Booking.wallet_id), reservationObject.getString(WebService.Booking.id_payment_way), reservationObject.getString(WebService.Booking.fcm_token), reservationObject.getString(WebService.Booking.id), WebService.Booking.bookingStateDone, reservationObject.getString(WebService.Booking.id_client), getActivity().getString(R.string.doctorFinished), reservationObject.getString(WebService.Booking.id_state));
                    }

                    if (currentTimeMillis < bookTotal - duration)
                        holder.call.setSupportImageTintList(getActivity().getResources().getColorStateList(R.color.silver));


                    holder.call.setImageResource(R.drawable.video_call);
                    holder.call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Calendar calendar = Calendar.getInstance();
                            final long currentTimeMillis = TimeUnit.HOURS.toMillis(calendar.get(Calendar.HOUR_OF_DAY)) +
                                    TimeUnit.DAYS.toMillis(calendar.get(Calendar.DAY_OF_MONTH)) + calendar.get(Calendar.DAY_OF_MONTH)
                                    + TimeUnit.MINUTES.toMillis(calendar.get(Calendar.MINUTE));

                            if (currentTimeMillis < bookTotal - duration) {
                                Toast.makeText(getContext(), getActivity().getString(R.string.waitToBookTime), Toast.LENGTH_LONG).show();
                                return;
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
                        ClientProfile fragment = new ClientProfile();
                        FragmentTransaction ft = fm.beginTransaction();
                        fragment.setArguments(bundle);
                        ft.replace(R.id.fragment, fragment, "ClientProfile");
                        ft.addToBackStack("ClientProfile");
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


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public com.example.snosey.balto.Support.CustomTextView firstName, date, kind, price;
        public AppCompatButton cancelOrStart, done, arrive;
        public AppCompatImageView logo, call;

        public MyViewHolder(View v) {
            super(v);
            firstName = (com.example.snosey.balto.Support.CustomTextView) v.findViewById(R.id.firstName);
            date = (com.example.snosey.balto.Support.CustomTextView) v.findViewById(R.id.date);
            kind = (com.example.snosey.balto.Support.CustomTextView) v.findViewById(R.id.kind);
            price = (com.example.snosey.balto.Support.CustomTextView) v.findViewById(R.id.price);
            cancelOrStart = (AppCompatButton) v.findViewById(R.id.cancelOrStart);
            done = (AppCompatButton) v.findViewById(R.id.done);
            arrive = (AppCompatButton) v.findViewById(R.id.arrive);
            call = (AppCompatImageView) v.findViewById(R.id.call);
            logo = (AppCompatImageView) v.findViewById(R.id.logo);
        }
    }


    private void getPercntageDoctor(final String orderId, final String id, final String totalPrice, String idSub, final String idUser, final String paymentWay) throws JSONException {
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
                addPaymentToDB(intTotalPrice, adminPrice, doctorPrice, "", id, idUser, paymentWay);
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Payment.doctorPercentageMoneyApi, urlData.get());
    }

    private void addPaymentToDB(int totalPrice, int adminPrice, int doctorPrice, String orderId, String id_booking, String idUser, String paymentWay) throws JSONException {

        UrlData urlData = new UrlData();
        urlData.add(WebService.Payment.type, WebService.Payment.depet);
        urlData.add(WebService.Payment.total_money, totalPrice + "");
        urlData.add(WebService.Payment.doctor_money, "-" + doctorPrice);
        urlData.add(WebService.Payment.admin_money, adminPrice + "");
        urlData.add(WebService.Payment.payMob_id, orderId);
        urlData.add(WebService.Payment.id_user, MainActivity.jsonObject.getString("id"));
        urlData.add(WebService.Payment.id_payment_way, paymentWay);
        urlData.add(WebService.Payment.id_booking, id_booking);
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(final String output) throws JSONException {

            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Payment.addPayment, urlData.get());
    }

    private void updateBooking(final String walletId, final String paymentId, final String fcm_token, final String id, final String state, final String id_client, final String title, final String previousState) {
        UrlData urlData = new UrlData();
        urlData.add(WebService.Booking.id, id);
        urlData.add(WebService.Booking.id_state, state);

        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                JSONObject jsonObjectBooking = new JSONObject(output).getJSONObject("booking");
                if (paymentId.equals(WebService.Booking.wallet)) {
                    UrlData urlData2 = new UrlData();
                    urlData2.add(WebService.Payment.id, walletId);
                    if ((state.equals(WebService.Booking.bookingStateDoctorCancel) ||
                            state.equals(WebService.Booking.bookingStateTimeout))) {
                        urlData2.add(WebService.Payment.state, WebService.Payment.refunded);
                    } else if (!previousState.equals(WebService.Booking.bookingStateProcessing) && state.equals(WebService.Booking.bookingStateDone)) {
                        urlData2.add(WebService.Payment.state, WebService.Payment.done);
                    }
                    new GetData(new GetData.AsyncResponse() {
                        @Override
                        public void processFinish(String output) throws JSONException {
                        }
                    }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Payment.updateWalletStateApi, urlData2.get());
                }

                if (!previousState.equals(WebService.Booking.bookingStateProcessing) &&
                        state.equals(WebService.Booking.bookingStateDone)) {
                    getPercntageDoctor("", jsonObjectBooking.getString(WebService.Booking.id), jsonObjectBooking.getString(WebService.Booking.total_price),
                            jsonObjectBooking.getString(WebService.Booking.id_sub), jsonObjectBooking.getString(WebService.Booking.id_client), jsonObjectBooking.getString(WebService.Booking.id_payment_way));

                    if (!paymentId.equals(WebService.Booking.wallet)) {
                        checkIfCodeExist(jsonObjectBooking.getString(WebService.Booking.total_price), jsonObjectBooking.getString(WebService.Booking.id_coupon_client),
                                jsonObjectBooking.getString(WebService.Booking.id_client));
                    }
                    sendNotification(fcm_token, state, id + "|" + MainActivity.jsonObject.getString("id"), title);
                    {
                        RateDialog rateDialog = new RateDialog(getActivity(), id, id_client);
                        rateDialog.show();
                        rateDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                Bundle bundle = new Bundle();
                                bundle.putString(WebService.Booking.id, id);
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                MedicalReport fragment = new MedicalReport();
                                FragmentTransaction ft = fm.beginTransaction();
                                fragment.setArguments(bundle);
                                ft.replace(R.id.fragment, fragment, "MedicalReport");
                                ft.addToBackStack("MedicalReport");
                                ft.commit();
                            }
                        });
                    }
                } else if (!state.equals(WebService.Booking.bookingStateTimeout))
                    sendNotification(fcm_token, state, MainActivity.jsonObject.getString("id"), title);

                getComingReservation();
            }
        }, getActivity(), false).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, WebService.Booking.updateBookingApi, urlData.get());
    }

    private void checkIfCodeExist(final String totalPrice, String id_coupon_client, final String idClient) {
        if (id_coupon_client.equals("0"))
            PAYNOW(totalPrice, idClient);
        else {
            UrlData urlData = new UrlData();
            urlData.add(WebService.PromoCode.id, id_coupon_client);
            new GetData(new GetData.AsyncResponse() {
                @Override
                public void processFinish(String output) throws JSONException {
                    JSONObject jsonObjectCode = new JSONObject(output).getJSONObject("coupon");
                    int price = Integer.parseInt(totalPrice) - (Integer.parseInt(totalPrice) * Integer.parseInt(jsonObjectCode.getString(WebService.PromoCode.discount))) / 100;
                    PAYNOW(price + "", idClient);

                }
            }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.PromoCode.promoCodeCheckApi, urlData.get());
        }
    }

    private void PAYNOW(final String latestPrice, final String idClient) {
        final RequestQueue MyRequestQueue = Volley.newRequestQueue(getActivity());
        UrlData urlData = new UrlData();
        urlData.add(WebService.Booking.id, idClient);
        urlData.add(WebService.Setting.default_location, "");
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                JSONObject jsonObject = new JSONObject(output).getJSONObject("user");
                try {

                    Log.e("output", output);
                    if (!jsonObject.getString("payment_token").equals("null") || !jsonObject.getString("payment_token").equals("")) {
                        payByCredit(latestPrice, idClient);
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

    @SuppressLint("RestrictedApi")
    @OnClick({R.id.day1, R.id.day2, R.id.day3, R.id.day4, R.id.day5, R.id.day6, R.id.day7})
    public void dayClick(View view) {
        setColorDefault();
        this.dayClick = ((AppCompatButton) view);
        this.dayClick.setTextColor(Color.WHITE);
        view.setBackgroundResource(R.drawable.circel);
        ((AppCompatButton) view).setSupportBackgroundTintList(getContext().getResources().getColorStateList(R.color.red));

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
        defaults.setSupportBackgroundTintList(getContext().getResources().getColorStateList(R.color.colorPrimary));
        defaults.setTextColor(Color.WHITE);
        defaults.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
    }

    String addZeroToString(String s) {
        if (s.length() == 1)
            s = "0" + s;
        return s;
    }

    void sendNotification(String regId, String type, String doctor_id, String title) {
        final UrlData urlData = new UrlData();

        urlData.add(WebService.Notification.reg_id, regId);
        urlData.add(WebService.Notification.data, doctor_id);
        urlData.add(WebService.Notification.kind, type);
        urlData.add(WebService.Notification.message, " ");
        urlData.add(WebService.Notification.title, title);

        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {

            }
        }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Notification.notificationApi, urlData.get());

    }

}
