package com.example.snosey.balto.main.reservations;

import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Snosey on 3/7/2018.
 */

public class ReservationPast extends Fragment {
    @InjectView(R.id.reservationRV)
    RecyclerView reservationRV;

    @InjectView(R.id.date)
    com.example.snosey.balto.Support.CustomTextView date;

    @InjectView(R.id.dateLL)
    LinearLayout dateLL;


    @InjectView(R.id.calenderLL)
    LinearLayout calenderLL;


    JSONArray reservationJsonArray;
    ReservationAdapter reservationAdapter;
    RecyclerView recyclerViewReservation;


    private Calendar now = Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reservation_list, container, false);
        ButterKnife.inject(this, view);
        dateLL.setVisibility(View.GONE);
        calenderLL.setVisibility(View.VISIBLE);

        final Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy");
        date.setText(getActivity().getString(R.string.filter));
        getComingReservation(addZeroToString(cal.get(Calendar.DAY_OF_MONTH) + ""), addZeroToString((cal.get(Calendar.MONTH) + 1) + ""), addZeroToString(cal.get(Calendar.YEAR) + ""), false);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        cal.set(year, monthOfYear, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy");
                        date.setText(sdf.format(cal.getTime()));
                        getComingReservation(addZeroToString(dayOfMonth + ""), addZeroToString((monthOfYear + 1) + ""), addZeroToString(year + ""), true);
                    }
                });

                dpd.setMaxDate(now);
                dpd.show(getActivity().getFragmentManager(), "");

            }
        });


        reservationJsonArray = new JSONArray();
        reservationAdapter = new ReservationAdapter();
        recyclerViewReservation = (RecyclerView) view.findViewById(R.id.reservationRV);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewReservation.setLayoutManager(layoutManager);
        recyclerViewReservation.setAdapter(reservationAdapter);


        return view;
    }

    private void getComingReservation(String day, String month, String year, boolean thisDay) {
        UrlData urlData = new UrlData();
        try {
            urlData.add(WebService.Booking.id_doctor, MainActivity.jsonObject.getString("id"));
            urlData.add(WebService.Booking.lang, RegistrationActivity.sharedPreferences.getString("lang", "en"));
            urlData.add(WebService.Booking.type, WebService.Booking.doctor);
            if (thisDay) {
                urlData.add(WebService.Booking.receive_day, day);
                urlData.add(WebService.Booking.receive_month, month);
                urlData.add(WebService.Booking.receive_year, year);
            }

            urlData.add(WebService.Booking.state, WebService.Booking.past);
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
                    .inflate(R.layout.reservation_row_past, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            try {

                final JSONObject reservationObject = reservationJsonArray.getJSONObject(position);
                holder.date.setText(reservationObject.getString(WebService.Booking.receive_hour) + ":" + reservationObject.getString(WebService.Booking.receive_minutes)
                        + " - " + reservationObject.getString(WebService.Booking.receive_day) + "/" + reservationObject.getString(WebService.Booking.receive_month) + "/" + reservationObject.getString(WebService.Booking.receive_year));

                holder.price.setText(reservationObject.getString(WebService.Booking.total_price) + " " + getActivity().getString(R.string.egp));
                holder.firstName.setText(reservationObject.getString(WebService.Booking.firstName));

                if (reservationObject.getString(WebService.Booking.id_state).equals(WebService.Booking.bookingStateDone)) {
                    holder.medicalReport.setVisibility(View.VISIBLE);
                } else
                    holder.medicalReport.setVisibility(View.GONE);

                if (reservationObject.getString(WebService.Booking.rate).contains("null")) {
                    holder.rate.setText(getActivity().getString(R.string.addRate));
                    holder.rate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                RateDialog rateDialog = new RateDialog(getActivity(), reservationObject.getString(WebService.Booking.id), reservationObject.getString(WebService.Booking.id_client));
                                rateDialog.show();
                                rateDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        getComingReservation("", "", "", false);
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                } else {
                    holder.rate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
                            try {
                                alertDialogBuilder.setMessage(reservationObject.getString(WebService.Booking.review)).setPositiveButton(R.string.mdtp_ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        UrlData urlData = new UrlData();
                                        try {
                                            urlData.add(WebService.Booking.id, reservationObject.getString(WebService.Booking.rateId));
                                            new GetData(new GetData.AsyncResponse() {
                                                @Override
                                                public void processFinish(String output) throws JSONException {
                                                    getComingReservation("", "", "", false);
                                                }
                                            }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Booking.deleteRateApi, urlData.get());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    holder.rate.setText(reservationObject.getString(WebService.Booking.rate));
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

                holder.kind.setText(reservationObject.getString(WebService.Booking.doctorKindName));

                holder.medicalReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        medicalReport(reservationObject);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        private void medicalReport(JSONObject reservationObject) {
            Bundle bundle = new Bundle();
            bundle.putString("json", reservationObject.toString());
            FragmentManager fm = getActivity().getSupportFragmentManager();
            MedicalReport fragment = new MedicalReport();
            FragmentTransaction ft = fm.beginTransaction();
            fragment.setArguments(bundle);
            ft.replace(R.id.fragment, fragment, "MedicalReport");
            ft.addToBackStack("MedicalReport");
            ft.commit();
        }

        @Override
        public int getItemCount() {
            return (int) reservationJsonArray.length();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public com.example.snosey.balto.Support.CustomTextView firstName, date, kind, rate, price;
        public Button medicalReport;
        public ImageView logo, call;

        public MyViewHolder(View v) {
            super(v);
            firstName = (com.example.snosey.balto.Support.CustomTextView) v.findViewById(R.id.firstName);
            date = (com.example.snosey.balto.Support.CustomTextView) v.findViewById(R.id.date);
            kind = (com.example.snosey.balto.Support.CustomTextView) v.findViewById(R.id.kind);
            price = (com.example.snosey.balto.Support.CustomTextView) v.findViewById(R.id.price);
            rate = (com.example.snosey.balto.Support.CustomTextView) v.findViewById(R.id.rate);
            medicalReport = (Button) v.findViewById(R.id.medicalReport);
            call = (ImageView) v.findViewById(R.id.call);
            logo = (ImageView) v.findViewById(R.id.logo);

            Typeface font = Typeface.createFromAsset(
                    getActivity().getAssets(),
                    "fonts/arial.ttf");
            firstName.setTypeface(font, Typeface.BOLD);


            rate.setTypeface(font, Typeface.BOLD);
            rate.setPaintFlags(rate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            kind.setTypeface(font, Typeface.BOLD);


        }
    }

    String addZeroToString(String s) {
        if (s.length() == 1)
            s = "0" + s;
        return s;
    }
}
