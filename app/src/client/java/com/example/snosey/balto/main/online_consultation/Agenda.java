package com.example.snosey.balto.main.online_consultation;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.example.snosey.balto.Support.CustomTextView;
import com.example.snosey.balto.Support.image.CircleTransform;
import com.example.snosey.balto.Support.notification.NotifyService;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.login.RegistrationActivity;
import com.example.snosey.balto.main.reservation.Reservations;
import com.example.snosey.balto.payment.MakePayMobApi;
import com.example.snosey.balto.payment.PaymentSlider;
import com.paymob.acceptsdk.IntentConstants;
import com.paymob.acceptsdk.PayResponseKeys;
import com.paymob.acceptsdk.SaveCardResponseKeys;
import com.paymob.acceptsdk.ToastMaker;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Snosey on 3/13/2018.
 */

public class Agenda extends android.support.v4.app.Fragment {


    String chooseDay, chooseYear, chooseMonth;

    AppCompatButton day;
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
    RecyclerView agendaRV;
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
    @InjectView(R.id.logo)
    ImageView logo;
    @InjectView(R.id.price)
    com.example.snosey.balto.Support.CustomTextView price;
    @InjectView(R.id.name)
    com.example.snosey.balto.Support.CustomTextView name;
    @InjectView(R.id.type)
    com.example.snosey.balto.Support.CustomTextView type;
    @InjectView(R.id.rate)
    com.example.snosey.balto.Support.CustomTextView rate;
    @InjectView(R.id.rateNumber)
    com.example.snosey.balto.Support.CustomTextView rateNumber;
    @InjectView(R.id.notAvailable)
    com.example.snosey.balto.Support.CustomTextView notAvailable;
    private Calendar currentDate;


    // Arbitrary number and used only in this activity. Change it as you wish.
    static final int ACCEPT_PAYMENT_REQUEST = 10;


    JSONArray agendaJsonArray;
    JSONArray bookingJsonArray;
    AgendaAdapter agendaAdapter;
    private JSONObject doctorObject;
    private AgendaAdapter.TimeScheduale timeSceduale;
    private int latestPrice;
    private String id_coupon_client = "";
    private String walletId = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.agenda, container, false);
        ButterKnife.inject(this, view);

        try {
            doctorObject = new JSONObject(getArguments().getString("json"));
            name.setText(doctorObject.getString(WebService.OnlineConsult.firstName) + " " +
                    doctorObject.getString(WebService.OnlineConsult.lastName));

            type.setText(doctorObject.getString(WebService.OnlineConsult.subName));

            price.setText(doctorObject.getJSONObject(WebService.OnlineConsult.price).getString(WebService.OnlineConsult.price)
                    + " " + getActivity().getString(R.string.egp));

            if (!doctorObject.getString("image").equals("")) {
                String imageLink = doctorObject.getString("image");
                if (!imageLink.startsWith("https://"))
                    imageLink = WebService.Image.fullPathImage + imageLink;
                Picasso.with(getContext()).load(imageLink).transform(new CircleTransform()).into(logo);
            }

            if (!doctorObject.getString(WebService.Slider.total_rate).equals("0") &&
                    !doctorObject.getString(WebService.Slider.total_rate).equals("null"))
                rate.setText(doctorObject.getString(WebService.Slider.total_rate));
            // totalRateText();

        } catch (JSONException e) {
            e.printStackTrace();
        }
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

        agendaJsonArray = new JSONArray();

        agendaRV = (RecyclerView) view.findViewById(R.id.agendaRV);
        GridLayoutManager layoutManager
                = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);

        agendaRV.setLayoutManager(layoutManager);

        dayClick(day1);

        return view;
    }

    @SuppressLint("RestrictedApi")
    @OnClick({R.id.day1, R.id.day2, R.id.day3, R.id.day4, R.id.day5, R.id.day6, R.id.day7})
    public void dayClick(View view) {
        setColorDefault();
        this.day = ((AppCompatButton) view);
        this.day.setTextColor(Color.WHITE);
        view.setBackgroundResource(R.drawable.circel);
        ((AppCompatButton) view).setSupportBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.red));

        String day = ((AppCompatButton) view).getText().toString();
        String month = "";
        String year = "";
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
        month = ((currentDate.get(Calendar.MONTH)) + 1) + "";
        year = (currentDate.get(Calendar.YEAR)) + "";
        getSchedule(day, month, year);
    }

    private void getSchedule(final String day, final String month, final String year) {
        UrlData urlData = new UrlData();
        try {
            urlData.add(WebService.Schedule.id_user, doctorObject.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String modifiedDay = day;
        if (day.length() == 1)
            modifiedDay = "0" + modifiedDay;
        urlData.add(WebService.Schedule.day, modifiedDay);

        String modifiedMonth = month;
        if (modifiedMonth.length() == 1)
            modifiedMonth = "0" + modifiedMonth;
        urlData.add(WebService.Schedule.month, modifiedMonth);

        urlData.add(WebService.Schedule.year, year);

        final String finalModifiedDay = modifiedDay;
        final String finalModifiedMonth = modifiedMonth;
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                //setAdapter // refreshIt
                try {
                    agendaJsonArray = new JSONObject(output).getJSONArray("schedule");
                    getBooking(finalModifiedDay, finalModifiedMonth, year);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Schedule.getScheduleDataApi, urlData.get());
    }

    private void getBooking(String modifiedDay, String modifiedMonth, String year) {
        chooseDay = modifiedDay;
        chooseMonth = modifiedMonth;
        chooseYear = year;
        UrlData urlData = new UrlData();
        try {
            urlData.add(WebService.OnlineConsult.id_doctor, doctorObject.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        urlData.add(WebService.Schedule.day, modifiedDay);

        urlData.add(WebService.Schedule.month, modifiedMonth);

        urlData.add(WebService.Schedule.year, year);

        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                //setAdapter // refreshIt
                try {
                    bookingJsonArray = new JSONObject(output).getJSONArray("booking_time");

                    try {
                        agendaAdapter = new AgendaAdapter();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    agendaRV.setAdapter(agendaAdapter);

                    //agendaAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.OnlineConsult.doctorBookingTimeApi, urlData.get());
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

    @SuppressLint("RestrictedApi")
    private void setDefaults(AppCompatButton defaults) {
        defaults.setSupportBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.whiteDark));
        defaults.setTextColor(Color.BLACK);
        defaults.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
    }

    String addZeroToString(String s) {
        if (s.length() == 1)
            s = "0" + s;
        return s;
    }

    private class AgendaAdapter extends RecyclerView.Adapter<MyViewHolder> {
        List<TimeScheduale> newAgendaList;
        int estimated_time = 0;


        void sort() {
            Collections.sort(newAgendaList, new Comparator<TimeScheduale>() {
                @Override
                public int compare(TimeScheduale lhs, TimeScheduale rhs) {
                    // TODO Auto-generated method stub

                    if (rhs.fromHour > lhs.fromHour)
                        return -1;
                    else if (rhs.fromHour == lhs.fromHour && rhs.fromMin > lhs.fromMin)
                        return -1;
                    else
                        return 1;
                }
            });

        }


        AgendaAdapter() throws JSONException {
            try {
                estimated_time = Integer.parseInt(doctorObject.getJSONObject("price").getString("estimated_time"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            newAgendaList = new ArrayList<>();
            int maxOfBooking;
            TimeScheduale scheduale = new TimeScheduale();
            Calendar calendarNow = Calendar.getInstance();

            calendarNow.add(Calendar.HOUR, +1);

            for (int i = 0; i < agendaJsonArray.length(); i++) {
                if (estimated_time != 0) {
                    JSONObject agendaJsonObject = agendaJsonArray.getJSONObject(i);
                    scheduale.fromHour = Integer.parseInt(agendaJsonObject.getString(WebService.Schedule.from_hour));
                    scheduale.toHour = Integer.parseInt(agendaJsonObject.getString(WebService.Schedule.to_hour));
                    scheduale.fromMin = Integer.parseInt(agendaJsonObject.getString(WebService.Schedule.from_minutes));
                    scheduale.toMin = Integer.parseInt(agendaJsonObject.getString(WebService.Schedule.to_minutes));
                    maxOfBooking = (scheduale.toHour * 60 +
                            scheduale.toMin) -
                            (scheduale.fromHour * 60 + scheduale.fromMin);
                    Log.e("maxOfBooking", maxOfBooking + "/" + estimated_time);
                        maxOfBooking = maxOfBooking / estimated_time;
                    Log.e("final maxOfBooking", maxOfBooking + "");
                    for (int k = 0; k < maxOfBooking; k++) {
                        Calendar from = Calendar.getInstance();
                        from.set(Calendar.HOUR_OF_DAY, scheduale.fromHour);
                        from.set(Calendar.MINUTE, scheduale.fromMin);
                        from.add(Calendar.MINUTE, estimated_time * (k));

                        Calendar to = Calendar.getInstance();
                        to.set(Calendar.HOUR_OF_DAY, scheduale.fromHour);
                        to.set(Calendar.MINUTE, scheduale.fromMin);
                        to.add(Calendar.MINUTE, estimated_time * (k + 1));

                        TimeScheduale modifedTime = new TimeScheduale();

                        modifedTime.fromHour = from.get(Calendar.HOUR_OF_DAY);
                        modifedTime.fromMin = from.get(Calendar.MINUTE);

                        modifedTime.estimated_time = estimated_time + "";

                        modifedTime.toHour = to.get(Calendar.HOUR_OF_DAY);
                        modifedTime.toMin = to.get(Calendar.MINUTE);

                        Log.e("modifedTime", modifedTime.toString());

                        Log.e("chooseDay", chooseDay + " " + calendarNow.get(Calendar.DAY_OF_MONTH));
                        Log.e("fromHour", modifedTime.fromHour + " " + calendarNow.get(Calendar.HOUR_OF_DAY));
                        Log.e("fromMin", modifedTime.fromMin + " " + calendarNow.get(Calendar.MINUTE));


                        Log.e("day vs chooseday", chooseDay + "," + addZeroToString(calendarNow.get(Calendar.DAY_OF_MONTH) + ""));
                        Boolean sameDay = chooseDay.equals(addZeroToString(calendarNow.get(Calendar.DAY_OF_MONTH) + ""));
                        Boolean afterTime = (calendarNow.get(Calendar.HOUR_OF_DAY) > modifedTime.fromHour);
                        Boolean sameHour = ((calendarNow.get(Calendar.HOUR_OF_DAY) == modifedTime.fromHour)
                                && calendarNow.get(Calendar.MINUTE) > modifedTime.fromMin);

                        Log.e("sameDay", sameDay + "");
                        Log.e("afterTime", afterTime + "");
                        Log.e("sameHour", sameHour + "");

                        Log.e("CalenderNow", calendarNow.get(Calendar.HOUR) + " ");
                       /* if (calendarNow.get(Calendar.HOUR) == 0) {
                            continue;
                        } else*/
                        if ((sameHour || afterTime) && sameDay) {
                            if (true)
                                continue;
                            Log.e("Agenda", "past");
                            modifedTime.past = true;
                            newAgendaList.add(modifedTime);
                        } else {
                            Log.e("Agenda", "coming");
                            newAgendaList.add(modifedTime);
                        }
                    }


                }
            }
            if (newAgendaList.size() == 0)
                notAvailable.setVisibility(View.VISIBLE);
            else
                notAvailable.setVisibility(View.GONE);
            sort();
        }

        private class TimeScheduale {
            int fromHour, fromMin, toHour, toMin;
            String estimated_time;
            boolean past = false;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, final int position) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.agenda_row, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final TimeScheduale timeScheduale = newAgendaList.get(position);
            final boolean[] clickAble = {true};
            if (timeScheduale.fromHour >= 12)
                holder.timeFrom.setText
                        (addZeroToString(timeScheduale.fromHour - 12 + "")
                                + ":" + addZeroToString(timeScheduale.fromMin + "") + " " + "PM");
            else
                holder.timeFrom.setText(addZeroToString(timeScheduale.fromHour + "") + ":" + addZeroToString(timeScheduale.fromMin + "") + " " + "AM");

            if (timeScheduale.toHour >= 12)
                holder.timeTo.setText(addZeroToString(timeScheduale.toHour - 12 + "") + ":" + addZeroToString(timeScheduale.toMin + "") + " " + "PM");
            else
                holder.timeTo.setText(addZeroToString(timeScheduale.toHour + "") + ":" + addZeroToString(timeScheduale.toMin + "") + " " + "AM");

            if (clickAble[0])
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickAble[0] = false;
                        // if (MainActivity.jsonObject.getString("payment_token").equals("null")
                        //          || MainActivity.jsonObject.getString("payment_token").equals("")) {

                        if (false) {
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            PaymentSlider fragment = new PaymentSlider();
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.replace(R.id.fragment, fragment, "payment");
                            ft.addToBackStack("payment");
                            ft.commit();
                            return;
                        } else {
                            final String[] amount = {"0"};
                            final Dialog dialog = new Dialog(getActivity());
                            dialog.setContentView(R.layout.payment_confirm);
                            final com.example.snosey.balto.Support.CustomTextView estimatedFare = (com.example.snosey.balto.Support.CustomTextView) dialog.findViewById(R.id.estimatedFare);
                            estimatedFare.setText(price.getText().toString());
                            com.example.snosey.balto.Support.CustomTextView time = (com.example.snosey.balto.Support.CustomTextView) dialog.findViewById(R.id.time);
                            final AppCompatEditText promoCode = (AppCompatEditText) dialog.findViewById(R.id.promoCode);
                            final RadioButton credit = (RadioButton) dialog.findViewById(R.id.credit);
                            final RadioButton wallet = (RadioButton) dialog.findViewById(R.id.wallet);
                            final RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup);
                            final CustomTextView saved_card = (CustomTextView) dialog.findViewById(R.id.saved_card);
                            UrlData urlData = new UrlData();
                            final JSONObject[] jsonObject = {new JSONObject()};
                            try {
                                urlData.add(WebService.Payment.id, MainActivity.jsonObject.getString(WebService.Payment.id));
                                urlData.add(WebService.Payment.lng, RegistrationActivity.sharedPreferences.getString("lang", "en"));
                                new GetData(new GetData.AsyncResponse() {
                                    @Override
                                    public void processFinish(String output) throws JSONException {
                                        jsonObject[0] = new JSONObject(output);
                                        showCardNumber();
                                        credit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                if (b) {
                                                    showCardNumber();
                                                } else {

                                                    try {
                                                        amount[0] = jsonObject[0].getString(WebService.Payment.total_amount);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    saved_card.setText(getActivity().getString(R.string.Settled) + " ( " + amount[0] + " " +
                                                            getActivity().getString(R.string.egp) + " ) ");
                                                    saved_card.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.money, 0, 0, 0);


                                                }
                                            }
                                        });
                                    }

                                    private void showCardNumber() {
                                        try {
                                            if (!MainActivity.jsonObject.getString(WebService.Login.payment_token).equals("") && !MainActivity.jsonObject.getString(WebService.Login.payment_token).equals("null")) {
                                                saved_card.setText(MainActivity.jsonObject.getString(WebService.Login.card_number));
                                                if (MainActivity.jsonObject.getString(WebService.Login.card_type).equals("MasterCard"))
                                                    saved_card.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.master_card, 0, 0, 0);
                                                else
                                                    saved_card.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.visa, 0, 0, 0);

                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Payment.selectUserTransactionApi, urlData.get());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            final AppCompatButton confirmCode = (AppCompatButton) dialog.findViewById(R.id.confirmCode);
                            final int tempLatestPrice = Integer.parseInt(price.getText().toString().replace(" " + getActivity().getString(R.string.egp), ""));
                            latestPrice = tempLatestPrice;
                            confirmCode.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (promoCode.getText().length() != 0) {
                                        View keyboard = getActivity().getCurrentFocus();
                                        if (keyboard != null) {
                                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(keyboard.getWindowToken(), 0);
                                        }
                                        UrlData urlDataPromo = new UrlData();
                                        urlDataPromo.add(WebService.PromoCode.code, promoCode.getText().toString());
                                        try {
                                            urlDataPromo.add(WebService.PromoCode.id_user, MainActivity.jsonObject.getString("id"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        new GetData(new GetData.AsyncResponse() {
                                            @Override
                                            public void processFinish(String output) {
                                                if (output.contains("false"))
                                                    Toast.makeText(getActivity(), getActivity().getString(R.string.codeIsInvalid), Toast.LENGTH_SHORT).show();
                                                else {
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(output).getJSONObject("coupon");
                                                        id_coupon_client = jsonObject.getString("id");
                                                        Toast.makeText(getActivity(), jsonObject.getString("coupon_text"), Toast.LENGTH_LONG).show();
                                                        latestPrice = tempLatestPrice - (tempLatestPrice * Integer.parseInt(jsonObject.getString(WebService.PromoCode.discount)) / 100);
                                                        estimatedFare.setText(latestPrice + " " + getActivity().getString(R.string.egp));
                                                        confirmCode.setVisibility(View.GONE);
                                                        promoCode.setEnabled(false);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }, getActivity(), true).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, WebService.PromoCode.promoCodeCheckApiNew, urlDataPromo.get());

                                    }
                                }
                            });
                            time.setText(holder.timeFrom.getText().toString() + " - " + holder.timeTo.getText().toString());


                            com.example.snosey.balto.Support.CustomTextView doctorKind = (com.example.snosey.balto.Support.CustomTextView) dialog.findViewById(R.id.doctorKind);
                            doctorKind.setText(type.getText().toString());
                            Button confirm = (Button) dialog.findViewById(R.id.confirm);
                            confirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    timeSceduale = timeScheduale;
                                    if (radioGroup.getCheckedRadioButtonId() == R.id.credit)
                                        makePayment(WebService.Booking.credit);
                                    else {
                                        try {
                                            try {
                                                Log.e("Amount", "LatestPrice:" + latestPrice + " , current Amount:" + Integer.parseInt(jsonObject[0].getString(WebService.Payment.total_amount)));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            if (latestPrice <= Integer.parseInt(amount[0])) {
                                                UrlData urlData = new UrlData();
                                                urlData.add(WebService.Payment.amount, "" + latestPrice);
                                                urlData.add(WebService.Payment.way, WebService.Booking.wallet);
                                                urlData.add(WebService.Payment.state, WebService.Payment.outstanding);
                                                urlData.add(WebService.Payment.user, MainActivity.jsonObject.getString(WebService.Payment.id));
                                                new GetData(new GetData.AsyncResponse() {
                                                    @Override
                                                    public void processFinish(String output) throws JSONException {
                                                        Log.e("Transaction output", output);
                                                        JSONObject object = new JSONObject(output);
                                                        if (object.getString(WebService.Payment.status).equals("0"))
                                                            Toast.makeText(getActivity(), object.getString(WebService.Payment.error), Toast.LENGTH_SHORT).show();
                                                        else {
                                                            walletId = object.getJSONObject("data").getString("id");
                                                            makePayment(WebService.Booking.wallet);
                                                        }
                                                    }
                                                }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Payment.addTransactionApi, urlData.get());
                                            } else {
                                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                                PaymentSlider fragment = new PaymentSlider();
                                                FragmentTransaction ft = fm.beginTransaction();
                                                ft.replace(R.id.fragment, fragment, "payment");
                                                ft.addToBackStack("payment");
                                                ft.commit();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        }

                    }
                });
            holder.bookedUp.setVisibility(View.GONE);
            Log.e("timeScheduale.past", timeScheduale.past + "");
            if (timeScheduale.past) {
                holder.itemView.setClickable(false);
                holder.itemView.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
            } else {
                holder.itemView.setClickable(true);
                holder.itemView.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
            }
            for (int i = 0; i < bookingJsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = bookingJsonArray.getJSONObject(i);

                    Log.e("timeScheduale.past", timeScheduale.past + "");
                    if (!jsonObject.getString(WebService.Booking.id_state).equals(WebService.Booking.bookingStateDoctorCancel)
                            && !jsonObject.getString(WebService.Booking.id_state).equals(WebService.Booking.bookingStatePatientCancel)
                            && jsonObject.getString(WebService.Booking.receive_hour).equals(addZeroToString(timeScheduale.fromHour + ""))
                            && jsonObject.getString(WebService.Booking.receive_minutes).equals(addZeroToString(timeScheduale.fromMin + ""))) {
                        holder.bookedUp.setVisibility(View.VISIBLE);
                        holder.itemView.setClickable(false);
                        if (jsonObject.getString(WebService.Booking.id_client).equals(MainActivity.jsonObject.getString("id")))
                            holder.itemView.setBackgroundColor(getActivity().getResources().getColor(R.color.black_8am2));
                        else {
                            holder.itemView.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
                        }
                        break;
                    } else {
                        holder.bookedUp.setVisibility(View.GONE);
                        Log.e("timeScheduale.past", timeScheduale.past + "");
                        if (timeScheduale.past) {
                            holder.itemView.setClickable(false);
                            holder.itemView.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
                        } else {
                            holder.itemView.setClickable(true);
                            holder.itemView.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                        }
                        //  holder.book.setVisibility(View.VISIBLE);
                        //  holder.bookedUp.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private void makePayment(String paymentType) {
            try {
                Log.e("payment_token", MainActivity.jsonObject.getString("payment_token"));
                // String finalPrice = price.getText().toString().substring(0, price.getText().toString().indexOf(" ")) + "00";
                if (paymentType.equals(WebService.Booking.wallet)) {
                    confirmRequest(timeSceduale, "", paymentType);
                } else if (!MainActivity.jsonObject.getString("payment_token").equals("null") && !MainActivity.jsonObject.getString("payment_token").equals("")) {
                    confirmRequest(timeSceduale, "", paymentType);
                    //  new MakePayMobApi(getActivity(), latestPrice + "00", Agenda.this, MainActivity.jsonObject.getString("payment_token"), WebService.Payment.payLive2);
                } else {
                    new MakePayMobApi(getActivity(), "100", Agenda.this, "", WebService.Payment.payLive1, WebService.Booking.credit);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return (int) newAgendaList.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public com.example.snosey.balto.Support.CustomTextView timeFrom, timeTo;
        public com.example.snosey.balto.Support.CustomTextView bookedUp;

        public MyViewHolder(View v) {
            super(v);
            timeFrom = (com.example.snosey.balto.Support.CustomTextView) v.findViewById(R.id.timeFrom);
            timeTo = (com.example.snosey.balto.Support.CustomTextView) v.findViewById(R.id.timeTo);
            bookedUp = (com.example.snosey.balto.Support.CustomTextView) v.findViewById(R.id.bookedUp);
        }

    }


    private void confirmRequest(final AgendaAdapter.TimeScheduale timeScheduale, final String orderId, String paymentWay) {
        final UrlData urlData = new UrlData();
        //  if (getArguments().containsKey(WebService.HomeVisit.promoCode))
        //    urlData.add(WebService.Booking.id_coupon_client, getArguments().getString(WebService.HomeVisit.promoCode));
        try {
            urlData.add(WebService.Booking.id_client, MainActivity.jsonObject.getString("id"));
            urlData.add(WebService.Booking.id_sub, doctorObject.getString(WebService.OnlineConsult.id_sub));
            urlData.add(WebService.Booking.total_price, doctorObject.getJSONObject(WebService.OnlineConsult.price).getString(WebService.OnlineConsult.price));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        urlData.add(WebService.Booking.id_payment_way, paymentWay);
        if (!id_coupon_client.equals("")) {
            increaseCoupon(id_coupon_client);
        }
        urlData.add(WebService.Booking.id_coupon_client, id_coupon_client);
        urlData.add(WebService.Booking.receive_year, chooseYear + "");
        urlData.add(WebService.Booking.wallet_id, walletId);
        urlData.add(WebService.Booking.receive_month, chooseMonth + "");
        urlData.add(WebService.Booking.receive_day, chooseDay + "");
        urlData.add(WebService.Booking.receive_hour, addZeroToString(timeScheduale.fromHour + ""));
        urlData.add(WebService.Booking.receive_minutes, addZeroToString(timeScheduale.fromMin + ""));
        urlData.add(WebService.Booking.id_doctor_kind, WebService.onlineConsult);
        urlData.add(WebService.Booking.duration, "" + timeScheduale.estimated_time);
        try {
            urlData.add(WebService.Booking.id_doctor, doctorObject.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        urlData.add(WebService.Booking.id_state, WebService.Booking.bookingStateProcessing);

        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (output.contains("true")) {
                    try {

                        if (false)
                            sendSMS("201222272346", "لديك حجز جديد");
                        final JSONObject jsonBooking = new JSONObject(output).getJSONObject("booking");

                        {
                            final UrlData urlData = new UrlData();

                            saveAlarm15Min(Integer.parseInt(chooseYear), Integer.parseInt(chooseMonth), Integer.parseInt(chooseDay),
                                    Integer.parseInt(addZeroToString(timeScheduale.fromHour + "")), Integer.parseInt(addZeroToString(timeScheduale.fromMin + ""))
                                    , Integer.parseInt(jsonBooking.getString(WebService.Booking.id)) + 15);


                            saveAlarmNow(Integer.parseInt(chooseYear), Integer.parseInt(chooseMonth), Integer.parseInt(chooseDay),
                                    Integer.parseInt(addZeroToString(timeScheduale.fromHour + "")), Integer.parseInt(addZeroToString(timeScheduale.fromMin + "")),
                                    Integer.parseInt(jsonBooking.getString(WebService.Booking.id)));

                            urlData.add(WebService.Notification.reg_id, doctorObject.getString("fcm_token"));
                            urlData.add(WebService.Notification.data, jsonBooking.getString(WebService.Booking.id));
                            urlData.add(WebService.Notification.kind, WebService.Notification.Types.bookingRequestOnline);
                            urlData.add(WebService.Notification.message, " ");
                            urlData.add(WebService.Notification.title, getActivity().getString(R.string.newReservation));
                            new GetData(new GetData.AsyncResponse() {
                                @Override
                                public void processFinish(String output) {
                                    FragmentManager fm = getActivity().getSupportFragmentManager();
                                    Reservations fragment = new Reservations();
                                    FragmentTransaction ft = fm.beginTransaction();
                                    ft.replace(R.id.fragment, fragment, "Reservations");
                                    ft.addToBackStack("Reservations");
                                    ft.commit();
                                }
                            }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Notification.notificationApi, urlData.get());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Booking.addBookingApi, urlData.get());


    }

    private void increaseCoupon(String id_coupon_client) {
        UrlData urlData = new UrlData();
        urlData.add(WebService.PromoCode.id, id_coupon_client);

        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                Log.e("IncreaseCode", output);
            }
        }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.PromoCode.increasePromoCodeUsageApi, urlData.get());
    }

    private void getPercntageDoctor(final String orderId, final String id, final String totalPrice) throws JSONException {
        UrlData urlData = new UrlData();
        urlData.add(WebService.Booking.id_sub, doctorObject.getString(WebService.Booking.id_sub));
        urlData.add(WebService.Booking.id_doctor_kind, doctorObject.getJSONObject("price").getString(WebService.Booking.id_doctor_kind));
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                JSONObject jsonObject = new JSONObject(output);
                int intTotalPrice = Integer.parseInt(totalPrice);
                int adminPrice = (int) (intTotalPrice * Double.parseDouble(jsonObject.getString(WebService.Payment.online_percentage)) / 100);
                int doctorPrice = intTotalPrice - adminPrice;
                addPaymentToDB(intTotalPrice, adminPrice, doctorPrice, orderId, id);
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Payment.doctorPercentageMoneyApi, urlData.get());
    }

    private void addPaymentToDB(int totalPrice, int adminPrice, int doctorPrice, String orderId, String id_booking) throws JSONException {

        UrlData urlData = new UrlData();
        urlData.add(WebService.Payment.type, WebService.Payment.depet);
        urlData.add(WebService.Payment.total_money, totalPrice + "");
        urlData.add(WebService.Payment.doctor_money, "-" + doctorPrice);
        urlData.add(WebService.Payment.admin_money, adminPrice + "");
        urlData.add(WebService.Payment.payMob_id, orderId);
        urlData.add(WebService.Payment.id_user, doctorObject.getString("id"));
        urlData.add(WebService.Payment.id_payment_way, WebService.Booking.credit);
        urlData.add(WebService.Payment.id_booking, id_booking);
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(final String output) throws JSONException {
                Toast.makeText(getActivity(), getActivity().getString(R.string.bookingRequestSent), Toast.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (output.contains("null"))
                            return;

                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        Reservations fragment = new Reservations();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.fragment, fragment, "Reservations");
                        ft.addToBackStack("Reservations");
                        ft.commit();

                    }
                }, Toast.LENGTH_LONG);
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Payment.addPayment, urlData.get());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 6666) {
            if (resultCode == 6666) {
                try {
                    JSONObject jsonObject = new JSONObject(data.getStringExtra("response"));
                    if (jsonObject.getString("success").equals("true"))
                        confirmRequest(timeSceduale, jsonObject.getString("id"), WebService.Booking.credit);
                    else
                        Toast.makeText(getContext(), getActivity().getString(android.R.string.httpErrorBadUrl), Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == ACCEPT_PAYMENT_REQUEST) {
            Bundle extras = null;
            try {
                extras = data.getExtras();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            if (resultCode == IntentConstants.USER_CANCELED) {
                // User canceled and did no payment request was fired
                ToastMaker.displayShortToast(getActivity(), "User canceled!!");
            } else if (resultCode == IntentConstants.MISSING_ARGUMENT) {
                // You forgot to pass an important key-value pair in the intent's extras
                ToastMaker.displayShortToast(getActivity(), "Missing Argument == " + extras.getString(IntentConstants.MISSING_ARGUMENT_VALUE));
            } else if (resultCode == IntentConstants.TRANSACTION_ERROR) {
                // An error occurred while handling an API's response
                ToastMaker.displayShortToast(getActivity(), "Reason == " + extras.getString(IntentConstants.TRANSACTION_ERROR_REASON));
            } else if (resultCode == IntentConstants.TRANSACTION_REJECTED) {
                // User attempted to pay but their transaction was rejected

                // Use the static keys declared in PayResponseKeys to extract the fields you want
                ToastMaker.displayShortToast(getActivity(), extras.getString(PayResponseKeys.DATA_MESSAGE));
            } else if (resultCode == IntentConstants.TRANSACTION_REJECTED_PARSING_ISSUE) {
                // User attempted to pay but their transaction was rejected. An error occured while reading the returned JSON
                ToastMaker.displayShortToast(getActivity(), extras.getString(IntentConstants.RAW_PAY_RESPONSE));
            } else if (resultCode == IntentConstants.TRANSACTION_SUCCESSFUL) {
                // User finished their payment successfully

                // Use the static keys declared in PayResponseKeys to extract the fields you want
                ToastMaker.displayShortToast(getActivity(), extras.getString(PayResponseKeys.DATA_MESSAGE));
                saveCard(extras.getString(SaveCardResponseKeys.TOKEN), extras.getString(SaveCardResponseKeys.CARD_SUBTYPE), extras.getString(SaveCardResponseKeys.MASKED_PAN));

            } else if (resultCode == IntentConstants.TRANSACTION_SUCCESSFUL_PARSING_ISSUE) {
                // User finished their payment successfully. An error occured while reading the returned JSON.
                ToastMaker.displayShortToast(getActivity(), "TRANSACTION_SUCCESSFUL - Parsing Issue");

                // ToastMaker.displayShortToast(getActivity(), extras.getString(IntentConstants.RAW_PAY_RESPONSE));
            } else if (resultCode == IntentConstants.TRANSACTION_SUCCESSFUL_CARD_SAVED) {

                saveCard(extras.getString(SaveCardResponseKeys.TOKEN), extras.getString(SaveCardResponseKeys.CARD_SUBTYPE), extras.getString(SaveCardResponseKeys.MASKED_PAN));

            } else if (resultCode == IntentConstants.USER_CANCELED_3D_SECURE_VERIFICATION) {
                ToastMaker.displayShortToast(getActivity(), "User canceled 3-d scure verification!!");

                // Note that a payment process was attempted. You can extract the original returned values
                // Use the static keys declared in PayResponseKeys to extract the fields you want
                ToastMaker.displayShortToast(getActivity(), extras.getString(PayResponseKeys.PENDING));
            } else if (resultCode == IntentConstants.USER_CANCELED_3D_SECURE_VERIFICATION_PARSING_ISSUE) {
                ToastMaker.displayShortToast(getActivity(), "User canceled 3-d scure verification - Parsing Issue!!");

                // Note that a payment process was attempted.
                // User finished their payment successfully. An error occured while reading the returned JSON.
                ToastMaker.displayShortToast(getActivity(), extras.getString(IntentConstants.RAW_PAY_RESPONSE));
            }
        }
    }


    private void saveAlarm15Min(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minute, int bookingId) {


        Calendar now = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
        calendar.add(Calendar.MINUTE, -15);
        calendar.add(Calendar.MONTH, -1);

        if (now.getTimeInMillis() > calendar.getTimeInMillis())
            return;

        Log.e("Save Alarm", calendar.getTime().toString());

        ComponentName receiver = new ComponentName(getActivity(), NotifyService.class);
        PackageManager pm = getActivity().getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent1 = new Intent(getActivity(), NotifyService.class);
        intent1.putExtra("now", false);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),
                bookingId, intent1,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                0, pendingIntent);


    }

    private void saveAlarmNow(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minute, int bookingId) {

        if (true)
            return;

        Calendar now = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
        calendar.add(Calendar.MONTH, -1);

        if (now.getTimeInMillis() > calendar.getTimeInMillis())
            return;

        Log.e("Save Alarm", calendar.getTime().toString());

        ComponentName receiver = new ComponentName(getActivity(), NotifyService.class);
        PackageManager pm = getActivity().getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent1 = new Intent(getActivity(), NotifyService.class);
        intent1.putExtra("now", true);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),
                bookingId, intent1,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                0, pendingIntent);


    }

    private void saveCard(String token, final String type, final String number) {
        UrlData urlData = new UrlData();
        urlData.add("payment_token", token);
        urlData.add(WebService.Login.card_number, number);
        urlData.add(WebService.Login.card_type, type);
        try {
            urlData.add("id", MainActivity.jsonObject.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                JSONObject jsonObject = new JSONObject(output);
                MainActivity.jsonObject.put("payment_token", jsonObject.getJSONObject("user").getString("payment_token"));
                MainActivity.jsonObject.put(WebService.Login.card_type, type);
                MainActivity.jsonObject.put(WebService.Login.card_number, number);
                // ToastMaker.displayShortToast(getActivity(), "Saved");
                confirmRequest(timeSceduale, "", WebService.Booking.credit);
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Setting.updateUserApi, urlData.get());

    }


    private void sendSMS(final String mobile, final String message) {
        UrlData urlData = new UrlData();
        urlData.add(WebService.SMS.language, "2");
        urlData.add(WebService.SMS.Username, WebService.SMS.usernameBalto);
        urlData.add(WebService.SMS.password, WebService.SMS.passwordBalto);
        urlData.add(WebService.SMS.sender, WebService.SMS.senderElbalto);
        urlData.add(WebService.SMS.Mobile, mobile);
        urlData.add(WebService.SMS.message, message);
        RequestQueue MyRequestQueue = Volley.newRequestQueue(getActivity());
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, WebService.SMS.url + urlData.get(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Pay Response", response);
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.

            }
        }
        ) {
            @Override
            public byte[] getBody() throws AuthFailureError {

                JSONObject MyData = new JSONObject();
                try {

                  /*  MyData.put(WebService.SMS.language, "2");
                    MyData.put(WebService.SMS.Username, WebService.SMS.usernameBalto);
                    MyData.put(WebService.SMS.password, WebService.SMS.passwordBalto);
                    MyData.put(WebService.SMS.sender, WebService.SMS.senderElbalto);
                    MyData.put(WebService.SMS.Mobile, mobile);
                    MyData.put(WebService.SMS.message, message);
                  */
                    Log.e("MyData", MyData.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return MyData.toString().getBytes();
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
}

