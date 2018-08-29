package com.example.snosey.balto.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.snosey.balto.MainActivity;
import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.image.CircleTransform;
import com.example.snosey.balto.Support.notification.NotifyService;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.login.RegistrationActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Snosey on 2/26/2018.
 */

public class ComingRequest extends Fragment {
    @InjectView(R.id.logo)
    ImageView logo;
    @InjectView(R.id.clientName)
    com.example.snosey.balto.Support.CustomTextView clientName;
    @InjectView(R.id.clientRate)
    com.example.snosey.balto.Support.CustomTextView clientRate;
    @InjectView(R.id.requestDescription)
    com.example.snosey.balto.Support.CustomTextView requestDescription;
    @InjectView(R.id.visitDate)
    com.example.snosey.balto.Support.CustomTextView visitDate;
    @InjectView(R.id.details)
    com.example.snosey.balto.Support.CustomTextView details;
    @InjectView(R.id.estimatedFare)
    com.example.snosey.balto.Support.CustomTextView estimatedFare;
    @InjectView(R.id.paymentWay)
    com.example.snosey.balto.Support.CustomTextView paymentWay;

    @InjectView(R.id.clientAddress)
    com.example.snosey.balto.Support.CustomTextView clientAddress;

    @InjectView(R.id.durationLayout)
    RelativeLayout durationLayout;

    @InjectView(R.id.serviceDuration)
    com.example.snosey.balto.Support.CustomTextView serviceDuration;

    @InjectView(R.id.accept)
    Button accept;

    @InjectView(R.id.cancel)
    com.example.snosey.balto.Support.CustomTextView cancel;
    private String receive_hour, receive_minutes, receive_day, receive_year, receive_month;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.coming_request, container, false);
        ButterKnife.inject(this, view);

        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.back)).setVisibility(View.VISIBLE);
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.menu)).setVisibility(View.GONE);
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.right_icon)).setVisibility(View.GONE);

        final UrlData urlData = new UrlData();
        urlData.add(WebService.Booking.type, RegistrationActivity.sharedPreferences.getString("lang", "en"));
        urlData.add(WebService.Booking.id_booking, getArguments().getString(WebService.Booking.id));

        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                try {
                    final JSONObject booking = new JSONObject(output).getJSONObject("booking");
                    if (booking.getString("id").equals("null"))
                        return;

                    if (booking.getString(WebService.Booking.duration).equals(""))
                        durationLayout.setVisibility(View.GONE);
                    else
                        serviceDuration.setText(booking.getString(WebService.Booking.duration));
                    clientAddress.setText(booking.getString(WebService.Booking.client_address));
                    clientAddress.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            String uri = null;
                            try {
                                uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)",
                                        Double.parseDouble(booking.getString(WebService.Booking.client_latitude)),
                                        Double.parseDouble(booking.getString(WebService.Booking.client_longitude)), "");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            intent.setPackage("com.google.android.apps.maps");
                            getActivity().startActivity(intent);


                        }
                    });
                    receive_hour = booking.getString(WebService.Booking.receive_hour);
                    receive_minutes = booking.getString(WebService.Booking.receive_minutes);
                    receive_day = booking.getString(WebService.Booking.receive_day);
                    receive_month = booking.getString(WebService.Booking.receive_month);
                    receive_year = booking.getString(WebService.Booking.receive_year);
                    estimatedFare.setText(booking.getString(WebService.Booking.total_price) + " " + getActivity().getString(R.string.egp));
                    visitDate.setText(receive_hour + ":" + receive_minutes + "  /   " + receive_day + "-" + receive_month + "-" + receive_year);

                    if (booking.getString(WebService.Booking.id_payment_way).equals("1"))
                        paymentWay.setText(getActivity().getString(R.string.cash));
                    else
                        paymentWay.setText(getActivity().getString(R.string.credit));

                    requestDescription.setText(booking.getString(WebService.Booking.subCategoryName));

                    clientName.setText(booking.getString(WebService.Booking.firstName));
                    if (!booking.getString(WebService.Booking.totalRate).equals("0") || !booking.getString(WebService.Booking.totalRate).equals("")
                            || !booking.getString(WebService.Booking.totalRate).equals("null"))
                        clientRate.setText(booking.getString(WebService.Booking.totalRate));

                    if (!booking.getString("image").equals("")) {
                        String imageLink = booking.getString("image");
                        if (!imageLink.startsWith("https://"))
                            imageLink = WebService.Image.fullPathImage + imageLink;
                        Picasso.with(getActivity()).load(imageLink).transform(new CircleTransform()).into(logo);
                    }


                    logo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle bundle = new Bundle();
                            try {
                                bundle.putString(WebService.HomeVisit.id_user, booking.getString(WebService.Booking.id_client));
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


                    cancel.setVisibility(View.GONE);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getActivity().onBackPressed();
                            // add in log
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Booking.getBookDataApi, urlData.get());


        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check if booking still available or no
                {
                    final UrlData urlData = new UrlData();
                    urlData.add(WebService.Booking.type, RegistrationActivity.sharedPreferences.getString("lang", "en"));
                    urlData.add(WebService.Booking.id_booking, getArguments().getString(WebService.Booking.id));
                    new GetData(new GetData.AsyncResponse() {
                        @Override
                        public void processFinish(String output) {
                            try {
                                JSONObject booking = new JSONObject(output).getJSONObject("booking");
                                if (booking.getString(WebService.Booking.id_state).equals("null") ||
                                        !booking.getString(WebService.Booking.id_state).equals(WebService.Booking.bookingStateSearch)) {
                                    Toast.makeText(getActivity(), getActivity().getString(R.string.requestNotAvailable), Toast.LENGTH_LONG).show();
                                } else {
                                    updateBooking();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Booking.getBookDataApi, urlData.get());
                }
            }
        });


        return view;
    }

    private void updateBooking() {
        UrlData updateData = new UrlData();
        try {
            updateData.add(WebService.Booking.id_doctor, MainActivity.jsonObject.getString("id"));
            updateData.add(WebService.Booking.id_state, WebService.Booking.bookingStateProcessing);
            updateData.add(WebService.Booking.id, getArguments().getString(WebService.Booking.id));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (output.contains("true")) {
                    saveAlarm15Min(Integer.parseInt(receive_year), Integer.parseInt(receive_month), Integer.parseInt(receive_day), Integer.parseInt(receive_hour), Integer.parseInt(receive_minutes), Integer.parseInt(getArguments().getString(WebService.Booking.id)) + 15);
                    saveAlarmNow(Integer.parseInt(receive_year), Integer.parseInt(receive_month), Integer.parseInt(receive_day), Integer.parseInt(receive_hour), Integer.parseInt(receive_minutes), Integer.parseInt(getArguments().getString(WebService.Booking.id)) + 15);
                    getActivity().onBackPressed();
                    //   sendNotification(booking.getString(WebService.Booking.fcm_token), getArguments().getString(WebService.Booking.id));
                }
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Booking.updateBookingApi, updateData.get());

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


    private void sendNotification(String fcm_token, String bookingId) {
        if (true)
            return;
        UrlData urlData = new UrlData();
        urlData.add(WebService.Notification.data, bookingId);
        urlData.add(WebService.Notification.kind, WebService.Notification.Types.acceptRequestHomeVisit);
        urlData.add(WebService.Notification.message, " ");
        urlData.add(WebService.Notification.title, "Accept Request");
        urlData.add(WebService.Notification.reg_id, fcm_token);
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {

            }
        }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Notification.notificationApi, urlData.get());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
