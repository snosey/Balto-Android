package com.example.snosey.balto.main.online_consultation;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
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
import com.example.snosey.balto.main.payment.MakePayMobApi;
import com.example.snosey.balto.main.payment.PaymentSlider;
import com.example.snosey.balto.main.reservation.Reservations;
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
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Snosey on 3/13/2018.
 */

public class Agenda extends android.support.v4.app.Fragment {


    String chooseDay, chooseYear, chooseMonth;

    Button day;
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
    RecyclerView agendaRV;
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
    @InjectView(R.id.logo)
    ImageView logo;
    @InjectView(R.id.price)
    TextView price;
    @InjectView(R.id.name)
    TextView name;
    @InjectView(R.id.type)
    TextView type;
    @InjectView(R.id.rate)
    TextView rate;
    @InjectView(R.id.rateNumber)
    TextView rateNumber;
    @InjectView(R.id.notAvailable)
    TextView notAvailable;
    private GregorianCalendar currentDate;


    // Arbitrary number and used only in this activity. Change it as you wish.
    static final int ACCEPT_PAYMENT_REQUEST = 10;


    JSONArray agendaJsonArray;
    JSONArray bookingJsonArray;
    AgendaAdapter agendaAdapter;
    private JSONObject doctorObject;
    private AgendaAdapter.TimeScheduale timeSceduale;

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

        agendaJsonArray = new JSONArray();

        agendaRV = (RecyclerView) view.findViewById(R.id.agendaRV);
        GridLayoutManager layoutManager
                = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);

        agendaRV.setLayoutManager(layoutManager);

        dayClick(day1);

        return view;
    }

    private void totalRateText() {
        UrlData urlData = new UrlData();
        try {
            urlData.add(WebService.Slider.id_user, doctorObject.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                if (!output.contains("null")) {
                    JSONArray allRate = new JSONObject(output).getJSONArray("rate");
                    rateNumber.setText(getActivity().getString(R.string.reviews) + ": " + allRate.length());
                    rateNumber.setPaintFlags(rateNumber.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                }
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Slider.allUserRateApi, urlData.get());
    }


    @OnClick({R.id.day1, R.id.day2, R.id.day3, R.id.day4, R.id.day5, R.id.day6, R.id.day7})
    public void dayClick(View view) {
        setColorDefault();
        this.day = ((Button) view);
        this.day.setTextColor(Color.WHITE);
        view.setBackgroundResource(R.drawable.circel);
        view.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.red));

        String day = ((TextView) view).getText().toString();
        String month = "";
        String year = "";
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

    private void setDefaults(Button defaults) {
        defaults.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.whiteDark));
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

        AgendaAdapter() throws JSONException {
            try {
                estimated_time = Integer.parseInt(doctorObject.getJSONObject("price").getString("estimated_time"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            newAgendaList = new ArrayList<>();
            int maxOfBooking;
            TimeScheduale scheduale = new TimeScheduale();
            Calendar calendarNow = new GregorianCalendar();
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
                        if (chooseDay.equals(calendarNow.get(Calendar.DAY_OF_MONTH) + "")
                                && (calendarNow.get(Calendar.HOUR_OF_DAY) > modifedTime.fromHour ||
                                (calendarNow.get(Calendar.HOUR_OF_DAY) == modifedTime.fromHour) &&
                                        calendarNow.get(Calendar.MINUTE) > modifedTime.fromMin))
                            continue;
                        newAgendaList.add(modifedTime);
                    }

                }
            }
            if (newAgendaList.size() == 0)
                notAvailable.setVisibility(View.VISIBLE);
            else
                notAvailable.setVisibility(View.GONE);


        }

        private class TimeScheduale {
            int fromHour, fromMin, toHour, toMin;
            String estimated_time;
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
            holder.timeFrom.setText(addZeroToString(timeScheduale.fromHour + "") + ":" + addZeroToString(timeScheduale.fromMin + ""));
            holder.timeTo.setText(addZeroToString(timeScheduale.toHour + "") + ":" + addZeroToString(timeScheduale.toMin + ""));
            if (clickAble[0])
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickAble[0] = false;
                        try {
                            if (MainActivity.jsonObject.getString("payment_token").equals("null") || MainActivity.jsonObject.getString("payment_token").equals("")) {
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                PaymentSlider fragment = new PaymentSlider();
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.replace(R.id.fragment, fragment, "payment");
                                ft.addToBackStack("payment");
                                ft.commit();
                                return;
                            } else {
                                final Dialog dialog = new Dialog(getActivity());
                                dialog.setContentView(R.layout.payment_confirm);

                                TextView time = (TextView) dialog.findViewById(R.id.time);
                                time.setText(holder.timeFrom.getText().toString() + " - " + holder.timeTo.getText().toString());

                                TextView estimatedFare = (TextView) dialog.findViewById(R.id.estimatedFare);
                                estimatedFare.setText(price.getText().toString());

                                TextView doctorKind = (TextView) dialog.findViewById(R.id.doctorKind);
                                doctorKind.setText(type.getText().toString());
                                Button confirm = (Button) dialog.findViewById(R.id.confirm);
                                confirm.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        timeSceduale = timeScheduale;
                                        makePayment();
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

            for (int i = 0; i < bookingJsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = bookingJsonArray.getJSONObject(i);

                    if (jsonObject.getString(WebService.Booking.receive_hour).equals(addZeroToString(timeScheduale.fromHour + "")) &&
                            jsonObject.getString(WebService.Booking.receive_minutes).equals(addZeroToString(timeScheduale.fromMin + ""))) {
                        //  holder.book.setVisibility(View.GONE);
                        //  holder.bookedUp.setVisibility(View.VISIBLE);

                        holder.bookedUp.setVisibility(View.VISIBLE);
                        holder.itemView.setClickable(false);
                        if (jsonObject.getString(WebService.Booking.id_client).equals(MainActivity.jsonObject.getString("id")))
                            holder.itemView.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.black_8am2));
                        else {
                            holder.itemView.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.red));
                        }
                        break;
                    } else {
                        holder.bookedUp.setVisibility(View.GONE);
                        holder.itemView.setClickable(true);
                        holder.itemView.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.colorPrimary));
                        //  holder.book.setVisibility(View.VISIBLE);
                        //  holder.bookedUp.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private void makePayment() {
            try {
                Log.e("Payment Token", MainActivity.jsonObject.getString("payment_token"));
                String finalPrice = price.getText().toString().substring(0, price.getText().toString().indexOf(" ")) + "00";
                if (!MainActivity.jsonObject.getString("payment_token").equals("null") || !MainActivity.jsonObject.getString("payment_token").equals(""))
                    new MakePayMobApi(getActivity(), finalPrice, Agenda.this, MainActivity.jsonObject.getString("payment_token"), "241");
                else {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    PaymentSlider fragment = new PaymentSlider();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.fragment, fragment, "payment");
                    ft.addToBackStack("payment");
                    ft.commit();
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

        public TextView timeFrom, timeTo;
        public TextView bookedUp;

        public MyViewHolder(View v) {
            super(v);
            timeFrom = (TextView) v.findViewById(R.id.timeFrom);
            timeTo = (TextView) v.findViewById(R.id.timeTo);
            bookedUp = (TextView) v.findViewById(R.id.bookedUp);
        }

    }


    private void confirmRequest(AgendaAdapter.TimeScheduale timeScheduale, final String orderId) {
        UrlData urlData = new UrlData();
        //  if (getArguments().containsKey(WebService.HomeVisit.promoCode))
        //    urlData.add(WebService.Booking.id_coupon_client, getArguments().getString(WebService.HomeVisit.promoCode));
        try {
            urlData.add(WebService.Booking.id_client, MainActivity.jsonObject.getString("id"));
            urlData.add(WebService.Booking.id_sub, doctorObject.getString(WebService.OnlineConsult.id_sub));
            urlData.add(WebService.Booking.total_price, doctorObject.getJSONObject(WebService.OnlineConsult.price).getString(WebService.OnlineConsult.price));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        urlData.add(WebService.Booking.id_payment_way, "2");
        urlData.add(WebService.Booking.receive_year, chooseYear + "");
        urlData.add(WebService.Booking.receive_month, chooseMonth + "");
        urlData.add(WebService.Booking.receive_day, chooseDay + "");
        urlData.add(WebService.Booking.receive_hour, addZeroToString(timeScheduale.fromHour + ""));
        urlData.add(WebService.Booking.receive_minutes, addZeroToString(timeScheduale.fromMin + ""));
        urlData.add(WebService.Booking.id_doctor_kind, WebService.onlineConsult);
        urlData.add(WebService.Booking.duration, "" + timeScheduale.estimated_time);

        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (output.contains("true")) {
                    try {
                        final JSONObject jsonBooking = new JSONObject(output).getJSONObject("booking");

                        {
                            final UrlData urlData = new UrlData();

                            urlData.add(WebService.Notification.reg_id, doctorObject.getString("fcm_token"));
                            urlData.add(WebService.Notification.data, "empty");
                            urlData.add(WebService.Notification.kind, WebService.Notification.Types.bookingRequestOnline);
                            urlData.add(WebService.Notification.message, "");
                            urlData.add(WebService.Notification.title, "");
                            new GetData(new GetData.AsyncResponse() {
                                @Override
                                public void processFinish(String output) {
                                    UrlData updateData = new UrlData();
                                    try {
                                        updateData.add(WebService.Booking.id_doctor, doctorObject.getString("id"));
                                        updateData.add(WebService.Booking.id_state, WebService.Booking.bookingStateProcessing);
                                        updateData.add(WebService.Booking.id, jsonBooking.getString("id"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    new GetData(new GetData.AsyncResponse() {
                                        @Override
                                        public void processFinish(String output) {
                                            {
                                                try {
                                                    getPercntageDoctor(orderId, new JSONObject(output).getJSONObject("booking").getString("id"),
                                                            new JSONObject(output).getJSONObject("booking").getString(WebService.Booking.total_price));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Booking.updateBookingApi, updateData.get());

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

    private void getPercntageDoctor(final String orderId, final String id, final String totalPrice) throws JSONException {
        UrlData urlData = new UrlData();
        urlData.add(WebService.Booking.id_sub, doctorObject.getString(WebService.Booking.id_sub));
        urlData.add(WebService.Booking.id_doctor_kind, doctorObject.getJSONObject("price").getString(WebService.Booking.id_doctor_kind));
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                JSONObject jsonObject = new JSONObject(output);
                int intTotalPrice = Integer.parseInt(totalPrice);
                int adminPrice = intTotalPrice * Integer.parseInt(jsonObject.getString(WebService.Payment.online_percentage)) / 100;
                int doctorPrice = intTotalPrice - adminPrice;
                addPaymentToDB(intTotalPrice, adminPrice, doctorPrice, orderId, id);
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Payment.doctorPercentageMoney, urlData.get());
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
                        confirmRequest(timeSceduale, jsonObject.getString("id"));
                    else
                        Toast.makeText(getContext(), getActivity().getString(R.string.error_null_cursor), Toast.LENGTH_LONG).show();

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
                confirmRequest(timeSceduale, extras.getString(SaveCardResponseKeys.ORDER_ID));
                ToastMaker.displayShortToast(getActivity(), extras.getString(PayResponseKeys.DATA_MESSAGE));
            } else if (resultCode == IntentConstants.TRANSACTION_SUCCESSFUL_PARSING_ISSUE) {
                // User finished their payment successfully. An error occured while reading the returned JSON.
                confirmRequest(timeSceduale, extras.getString(SaveCardResponseKeys.ORDER_ID));
                ToastMaker.displayShortToast(getActivity(), "TRANSACTION_SUCCESSFUL - Parsing Issue");

                // ToastMaker.displayShortToast(getActivity(), extras.getString(IntentConstants.RAW_PAY_RESPONSE));
            } else if (resultCode == IntentConstants.TRANSACTION_SUCCESSFUL_CARD_SAVED) {
                confirmRequest(timeSceduale, extras.getString(SaveCardResponseKeys.ORDER_ID));
                UrlData urlData = new UrlData();
                urlData.add("payment_token", extras.getString(SaveCardResponseKeys.TOKEN));
                try {
                    urlData.add("id", MainActivity.jsonObject.getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new GetData(new GetData.AsyncResponse() {
                    @Override
                    public void processFinish(String output) throws JSONException {
                        ToastMaker.displayShortToast(getActivity(), "Saved");
                    }
                }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Setting.updateUserApi, urlData.get());
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


}

