package com.example.snosey.balto.main;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.snosey.balto.BuildConfig;
import com.example.snosey.balto.MainActivity;
import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.login.RegistrationActivity;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Snosey on 3/20/2018.
 */

public class MedicalReport extends Fragment {
    @InjectView(R.id.fullName)
    com.example.snosey.balto.Support.CustomTextView fullName;
    @InjectView(R.id.type)
    com.example.snosey.balto.Support.CustomTextView type;
    @InjectView(R.id.date)
    com.example.snosey.balto.Support.CustomTextView date;
    @InjectView(R.id.patientName)
    com.example.snosey.balto.Support.CustomTextView patientName;
    @InjectView(R.id.diagnosis)
    EditText diagnosis;
    @InjectView(R.id.prescription)
    EditText prescription;
    @InjectView(R.id.next)
    ImageButton next;
    private String json = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.medical_report, container, false);
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.back)).setVisibility(View.VISIBLE);
        ((RelativeLayout) getActivity().getWindow().getDecorView().findViewById(R.id.menuHome)).setVisibility(View.GONE);

        ButterKnife.inject(this, view);
        try {
            if (getArguments().containsKey("json")) {
                json = getArguments().getString("json");
                setData();
            } else {
                getData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void getData() {
        UrlData urlData = new UrlData();
        urlData.add(WebService.Booking.id_booking, getArguments().getString(WebService.Booking.id));
        urlData.add(WebService.Booking.lang, RegistrationActivity.sharedPreferences.getString("lang", "en"));
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                json = new JSONObject(output).getJSONObject("booking").toString();
                setData();
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Booking.getBookDataApi, urlData.get());
    }


    private void setData() throws JSONException {
        final JSONObject bookingObject = new JSONObject(json);
        if (BuildConfig.APPLICATION_ID.contains("doctor")) {
            diagnosis.setEnabled(true);
            prescription.setEnabled(true);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (getActivity().getCurrentFocus() != null) {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                        updateData(bookingObject.getString(WebService.Booking.id));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            next.setVisibility(View.GONE);
        }
        if (BuildConfig.APPLICATION_ID.contains("doctor")) {
            patientName.setText(bookingObject.getString(WebService.Booking.firstName) + " " + bookingObject.getString(WebService.Booking.lastName));
            fullName.setText(MainActivity.jsonObject.getString(WebService.SignUp.first_name_ar) + " " + MainActivity.jsonObject.getString(WebService.SignUp.last_name_ar));
        } else {
            patientName.setText(MainActivity.jsonObject.getString(WebService.SignUp.first_name_ar) + " " + MainActivity.jsonObject.getString(WebService.SignUp.last_name_ar));
            fullName.setText(bookingObject.getString(WebService.Booking.firstName) + " " + bookingObject.getString(WebService.Booking.lastName));
        }
        diagnosis.setText(bookingObject.getString(WebService.Booking.diagnosis));
        prescription.setText(bookingObject.getString(WebService.Booking.medication));
        type.setText(bookingObject.getString(WebService.Booking.subCategoryName));
        date.setText(bookingObject.getString(WebService.Booking.receive_day) + "/" + bookingObject.getString(WebService.Booking.receive_month) + "/" + bookingObject.getString(WebService.Booking.receive_year));
    }


    private void updateData(String id) {
        UrlData urlData = new UrlData();
        urlData.add(WebService.Booking.id, id);
        urlData.add(WebService.Booking.diagnosis, diagnosis.getText().toString());
        urlData.add(WebService.Booking.medication, prescription.getText().toString());
        // urlData.add(WebService.Booking.id_state, WebService.Booking.bookingStateDone);

        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                getActivity().onBackPressed();
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Booking.updateBookingApi, urlData.get());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
