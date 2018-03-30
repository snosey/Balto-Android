package com.example.snosey.balto.main.home_visit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snosey.balto.MainActivity;
import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.notification.NotifyService;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.main.reservation.Reservations;
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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Snosey on 2/21/2018.
 */

public class SendRequest extends Fragment {

    @InjectView(R.id.estimatedFare)
    TextView estimatedFare;
    @InjectView(R.id.paymentWay)
    TextView paymentWay;
    @InjectView(R.id.icon)
    ImageView icon;
    @InjectView(R.id.iconText)
    TextView iconText;

    private Marker mCurrLocationMarker;

    boolean doctorAvailable = false;
    JSONArray doctorJsonArray;

    private int mInterval = 20000; // 20 seconds by default, can be changed later
    private Handler mHandler;
    Runnable sendRequestLoop;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_visit_send_request, container, false);
        ButterKnife.inject(this, view);

        NearestDoctors();
        Picasso.with(getContext()).load(getArguments().getString("logo")).into(icon);
        iconText.setText(getArguments().getString("name"));
        estimatedFare.setText(getArguments().getString(WebService.HomeVisit.totalPrice));
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

                //move map camera
                mGoogleMap.setMapStyle(new MapStyleOptions(WebService.MapStyle.mapStyle));
                //move map camera
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        });

        return view;
    }

    private void NearestDoctors() {
        UrlData urlData = new UrlData();
        urlData.add(WebService.HomeVisit.latitude, "" + getArguments().getDouble("lat"));
        urlData.add(WebService.HomeVisit.longitude, "" + getArguments().getDouble("lng"));
        urlData.add(WebService.HomeVisit.distance, "10");
        urlData.add(WebService.HomeVisit.id_gender, getArguments().getString(WebService.HomeVisit.id_gender));
        urlData.add(WebService.HomeVisit.id_sub, getArguments().getString(WebService.HomeVisit.id_sub));
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                try {
                    doctorJsonArray = new JSONObject(output).getJSONArray("user");
                    if (doctorJsonArray.length() != 0)
                        doctorAvailable = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.HomeVisit.nearestDoctorApi, urlData.get());


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.schedule)
    public void onScheduleClicked() {
        final Calendar now = Calendar.getInstance();
        final Calendar nextWeek = Calendar.getInstance();
        nextWeek.setTime(new Date());
        nextWeek.add(Calendar.DAY_OF_YEAR, +7);

        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, final int year, final int monthOfYear, final int dayOfMonth) {
                {
                    {
                        TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                                if (doctorAvailable) {

                                    saveBooking(year, monthOfYear, dayOfMonth, hourOfDay, minute, "not now");
                                } else
                                    Toast.makeText(getActivity(), getActivity().getString(R.string.noProfissional), Toast.LENGTH_LONG).show();
                            }
                        }, false).show(getActivity().getFragmentManager(), "");
                    }
                }
            }
        }, now.get(Calendar.DAY_OF_YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.setMinDate(now);
        datePickerDialog.setMaxDate(nextWeek);
        datePickerDialog.show(getActivity().getFragmentManager(), "");
    }

    private void saveBooking(final int year, final int monthOfYear, final int dayOfMonth, final int hourOfDay, final int minute, final String type) {

        final int finalMonthOfYear=monthOfYear+1;
        UrlData urlData = new UrlData();
        if (getArguments().containsKey(WebService.HomeVisit.promoCode))
            urlData.add(WebService.Booking.id_coupon_client, getArguments().getString(WebService.HomeVisit.promoCode));

        try {
            urlData.add(WebService.Booking.id_client, MainActivity.jsonObject.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        urlData.add(WebService.Booking.id_sub, getArguments().getString(WebService.HomeVisit.id_sub));
        urlData.add(WebService.Booking.id_payment_way, getArguments().getString(WebService.Booking.id_payment_way));
        urlData.add(WebService.Booking.receive_year, year + "");
        urlData.add(WebService.Booking.receive_month, addZeroToString(finalMonthOfYear + ""));
        urlData.add(WebService.Booking.receive_day, addZeroToString(dayOfMonth + ""));
        urlData.add(WebService.Booking.receive_hour, hourOfDay + "");
        urlData.add(WebService.Booking.receive_minutes, minute + "");
        urlData.add(WebService.Booking.id_doctor_kind, WebService.homeVisit);
        urlData.add(WebService.Booking.duration, "" + getArguments().getString(WebService.HomeVisit.duration));
        urlData.add(WebService.Booking.total_price, estimatedFare.getText().toString());
        urlData.add(WebService.Booking.client_latitude, "" + getArguments().getDouble("lat"));
        urlData.add(WebService.Booking.client_longitude, "" + getArguments().getDouble("lng"));
        urlData.add(WebService.Booking.client_address, "" + getArguments().getString("address"));

        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (output.contains("true")) {
                    try {
                        JSONObject jsonObject = new JSONObject(output);
                        SearchForDoctor(jsonObject.getJSONObject("booking").getString("id"), type, year, monthOfYear, dayOfMonth, hourOfDay, minute);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Booking.addBookingApi, urlData.get());
    }

    private void SearchForDoctor(final String bookingId, final String type, final int year, final int monthOfYear, final int dayOfMonth, final int hourOfDay, final int minute) {
        final UrlData urlData = new UrlData();
        urlData.add(WebService.Booking.id_booking, bookingId);
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage(getActivity().getString(R.string.searching));
        dialog.show();
        dialog.setCancelable(false);
        final int[] index = {0};
        mHandler = new Handler();
        sendRequestLoop = new Runnable() {
            @Override
            public void run() {

                if (doctorAvailable)
                    new GetData(new GetData.AsyncResponse() {
                        @Override
                        public void processFinish(String output) {
                            try {
                                JSONObject jsonObject = new JSONObject(output).getJSONObject("booking");
                                if (!jsonObject.getString("id").equals("null")) {
                                    if (!jsonObject.getString(WebService.Booking.id_state).equals(WebService.Booking.bookingStateSearch)) {
                                        Log.e("state", "doctor accepted");
                                        if (type.equals("not now")) {
                                            //      saveAlarm(year, monthOfYear, dayOfMonth, hourOfDay, minute);
                                        }
                                        FragmentManager fm = getActivity().getSupportFragmentManager();
                                        FragmentTransaction ft = fm.beginTransaction();
                                        Reservations fragment = new Reservations();
                                        ft.addToBackStack("ReservationsMain");
                                        ft.replace(R.id.fragment, fragment);
                                        ft.commit();

                                        mHandler.removeCallbacks(sendRequestLoop);
                                        dialog.dismiss();
                                    } else {
                                        if (index[0] == 1) {
                                            deleteBooking(bookingId);
                                            mHandler.removeCallbacks(sendRequestLoop);
                                            dialog.dismiss();
                                            Toast.makeText(getActivity(), getActivity().getString(R.string.ProfissionalBusy), Toast.LENGTH_LONG).show();
                                        } else {
                                            sendForDoctor(bookingId);
                                            index[0]++;
                                        }
                                    }
                                } else {
                                    mHandler.removeCallbacks(sendRequestLoop);
                                    dialog.dismiss();
                                }

                            } catch (JSONException e) {
                                dialog.dismiss();
                                e.printStackTrace();
                            }
                        }
                    }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Booking.getBookDataApi, urlData.get());


            }
        };

        sendRequestLoop.run();
        mHandler.postDelayed(sendRequestLoop, mInterval);
    }

    private void saveAlarm(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minute) {


        Calendar now = new GregorianCalendar();
        Calendar calendar = new GregorianCalendar();
        calendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
        //calendar.add(Calendar.MINUTE, -1);
        long delay = SystemClock.elapsedRealtime() + (calendar.getTimeInMillis() - now.getTimeInMillis());

        Intent myIntent = new Intent(getActivity(), NotifyService.class);
        myIntent.putExtra("kind", WebService.Notification.Types.alarm);

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, delay, pendingIntent);
        Log.e("time in second", (calendar.getTimeInMillis() - now.getTimeInMillis()) / 1000 + "");

    }

    private void deleteBooking(String bookingId) {
        UrlData urlData = new UrlData();
        urlData.add(WebService.Booking.id, bookingId);
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {

            }
        }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Booking.deleteBooking, urlData.get());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mHandler.removeCallbacks(sendRequestLoop);
        } catch (Exception e) {

        }
    }

    private void sendForDoctor(String bookingId) {
        try {
            UrlData urlData = new UrlData();

            for (int i = 0; i < doctorJsonArray.length(); i++)
                urlData.add(WebService.Notification.reg_id, doctorJsonArray.getJSONObject(i).getString("fcm_token"));

            urlData.add(WebService.Notification.data, bookingId);
            urlData.add(WebService.Notification.kind, WebService.Notification.Types.bookingRequest);
            urlData.add(WebService.Notification.message, "");
            urlData.add(WebService.Notification.title, "");
            new GetData(new GetData.AsyncResponse() {
                @Override
                public void processFinish(String output) {

                }
            }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Notification.notificationApi, urlData.get());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.requestNow)
    public void onRequestNowClicked() {
        if (doctorAvailable) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 15);
            saveBooking(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), "now");
        } else
            Toast.makeText(getActivity(), getActivity().getString(R.string.noProfissional), Toast.LENGTH_LONG).show();

    }
    String addZeroToString(String s) {
        if (s.length() == 1)
            s = "0" + s;
        return s;
    }

}
