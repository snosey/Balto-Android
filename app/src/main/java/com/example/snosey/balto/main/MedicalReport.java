package com.example.snosey.balto.main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.snosey.balto.BuildConfig;
import com.example.snosey.balto.MainActivity;
import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Snosey on 3/20/2018.
 */

public class MedicalReport extends Fragment {
    @InjectView(R.id.fullName)
    TextView fullName;
    @InjectView(R.id.type)
    TextView type;
    @InjectView(R.id.date)
    TextView date;
    @InjectView(R.id.patientName)
    TextView patientName;
    @InjectView(R.id.diagnosis)
    EditText diagnosis;
    @InjectView(R.id.prescription)
    EditText prescription;
    @InjectView(R.id.next)
    ImageButton next;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.medical_report, container, false);
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.back)).setVisibility(View.VISIBLE);
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.menu)).setVisibility(View.GONE);

        ButterKnife.inject(this, view);
        try {
            setData(getArguments().getString(WebService.Booking.id));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }


    private void setData(final String id) throws JSONException {
        UrlData urlData = new UrlData();
        if (BuildConfig.APPLICATION_ID.contains("doctor")) {
            diagnosis.setEnabled(true);
            prescription.setEnabled(true);
            urlData.add(WebService.Booking.id_doctor, MainActivity.jsonObject.getString("id"));
            urlData.add(WebService.Booking.type, WebService.Booking.doctor);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateData(id);
                }
            });
        } else {
            urlData.add(WebService.Booking.id_client, MainActivity.jsonObject.getString("id"));
            urlData.add(WebService.Booking.type, WebService.Booking.client);
            next.setVisibility(View.GONE);
        }
        urlData.add(WebService.Booking.id_booking, id);
        urlData.add(WebService.Booking.type, Locale.getDefault().getLanguage());

        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                JSONObject bookingObject = new JSONObject(output).getJSONObject("booking");
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
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Booking.getBookDataApi, urlData.get());
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
