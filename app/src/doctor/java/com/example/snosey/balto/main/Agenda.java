package com.example.snosey.balto.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.snosey.balto.MainActivity;
import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Snosey on 3/5/2018.
 */

public class Agenda extends Fragment {

    AppCompatButton day;
    private Calendar currentDate;

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
    @InjectView(R.id.agendaRV)
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

    JSONArray agendaJsonArray;
    AgendaAdapter agendaAdapter;
    RecyclerView recyclerViewAgenda;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.agenda, container, false);
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.back)).setVisibility(View.VISIBLE);
        ((RelativeLayout) getActivity().getWindow().getDecorView().findViewById(R.id.menuHome)).setVisibility(View.GONE);
        ButterKnife.inject(this, view);
        setDate();
        agendaJsonArray = new JSONArray();
        agendaAdapter = new AgendaAdapter();
        recyclerViewAgenda = (RecyclerView) view.findViewById(R.id.agendaRV);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewAgenda.setLayoutManager(layoutManager);
        recyclerViewAgenda.setAdapter(agendaAdapter);

        dayClick(day1);
        return view;
    }


    private class AgendaAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, final int position) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.create_appointment_row, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            try {
                final JSONObject agenda = agendaJsonArray.getJSONObject(position);
                holder.update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        {
                            final Dialog dialog = new Dialog(getContext());
                            dialog.setContentView(R.layout.create_appointment);
                            final TimePicker timeFrom = (TimePicker) dialog.findViewById(R.id.timeFrom);
                            timeFrom.setIs24HourView(true);

                            final TimePicker timeTo = (TimePicker) dialog.findViewById(R.id.timeTo);
                            timeTo.setIs24HourView(true);

                            try {
                                timeFrom.setCurrentHour(Integer.parseInt(agenda.getString(WebService.Schedule.from_hour)));
                                timeFrom.setCurrentMinute(Integer.parseInt(agenda.getString(WebService.Schedule.from_minutes)));


                                timeTo.setCurrentHour(Integer.parseInt(agenda.getString(WebService.Schedule.to_hour)));
                                timeTo.setCurrentMinute(Integer.parseInt(agenda.getString(WebService.Schedule.to_minutes)));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Button confirm = (Button) dialog.findViewById(R.id.confirm);
                            confirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.hide();
                                    if (((timeFrom.getCurrentHour())
                                            > (timeTo.getCurrentHour())) ||
                                            !DataAvailable(timeFrom.getCurrentHour(), timeTo.getCurrentHour())) {
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                        alertDialogBuilder.setMessage(getActivity().getString(R.string.wrongAppoinment)).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        }).show();
                                    } else {
                                        UrlData urlData = new UrlData();
                                        try {
                                            urlData.add(WebService.Schedule.id_user, MainActivity.jsonObject.getString("id"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        try {
                                            urlData.add(WebService.Schedule.id, agenda.getString("id"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        urlData.add(WebService.Schedule.day, addZeroToString(day.getText().toString()));
                                        urlData.add(WebService.Schedule.month, addZeroToString((((currentDate.get(Calendar.MONTH)) + 1) + "")));
                                        urlData.add(WebService.Schedule.year, ((currentDate.get(Calendar.YEAR)) + ""));
                                        urlData.add(WebService.Schedule.from_hour, addZeroToString(timeFrom.getCurrentHour() + ""));
                                        urlData.add(WebService.Schedule.from_minutes, addZeroToString(timeFrom.getCurrentMinute() + ""));
                                        urlData.add(WebService.Schedule.to_hour, addZeroToString(timeTo.getCurrentHour() + ""));
                                        urlData.add(WebService.Schedule.to_minutes, addZeroToString(timeTo.getCurrentMinute() + ""));

                                        new GetData(new GetData.AsyncResponse() {
                                            @Override
                                            public void processFinish(String output) {
                                                //check if true
                                                dayClick(day);
                                            }
                                        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Schedule.updateScheduleApi, urlData.get());
                                    }
                                }

                                private boolean DataAvailable(Integer currentHour, Integer currentHour1) {
                                    boolean valid = true;
                                    //check if data is available
                                    for (int i = 0; i < agendaJsonArray.length(); i++) {
                                        try {
                                            JSONObject jsonAgenda = agendaJsonArray.getJSONObject(i);
                                            if (jsonAgenda.getString("id").equals(agenda.getString("id")))
                                                continue;

                                            if (currentHour >= Integer.parseInt(jsonAgenda.getString(WebService.Schedule.from_hour))
                                                    && currentHour <= Integer.parseInt(jsonAgenda.getString(WebService.Schedule.to_hour))) {
                                                valid = false;
                                                break;
                                            } else if (currentHour1 >= Integer.parseInt(jsonAgenda.getString(WebService.Schedule.from_hour))
                                                    && currentHour1 <= Integer.parseInt(jsonAgenda.getString(WebService.Schedule.to_hour))) {
                                                valid = false;
                                                break;
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    return valid;
                                }
                            });
                            dialog.show();
                        }
                    }
                });
                holder.timeFrom.setText(addZeroToString(agenda.getString(WebService.Schedule.from_hour)) + ":" + addZeroToString(agenda.getString(WebService.Schedule.from_minutes)));
                holder.timeTo.setText(addZeroToString(agenda.getString(WebService.Schedule.to_hour)) + ":" + addZeroToString(agenda.getString(WebService.Schedule.to_minutes)));
                holder.cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setMessage(R.string.areYouSure).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                UrlData urlData = new UrlData();
                                try {
                                    urlData.add(WebService.Schedule.id, agenda.getString("id"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                new GetData(new GetData.AsyncResponse() {
                                    @Override
                                    public void processFinish(String output) {
                                        if (output.contains("success"))
                                            dayClick(day);
                                    }
                                }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Schedule.deleteScheduleApi, urlData.get());
                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return (int) agendaJsonArray.length();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public com.example.snosey.balto.Support.CustomTextView timeFrom, timeTo;
        public Button update, cancel;

        public MyViewHolder(View v) {
            super(v);
            timeFrom = (com.example.snosey.balto.Support.CustomTextView) v.findViewById(R.id.timeFrom);
            timeTo = (com.example.snosey.balto.Support.CustomTextView) v.findViewById(R.id.timeTo);
            update = (Button) v.findViewById(R.id.update);
            cancel = (Button) v.findViewById(R.id.cancel);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @SuppressLint("RestrictedApi")
    @OnClick({R.id.day1, R.id.day2, R.id.day3, R.id.day4, R.id.day5, R.id.day6, R.id.day7})
    public void dayClick(View view) {
        setColorDefault();
        this.day = ((AppCompatButton) view);
        this.day.setTextColor(Color.WHITE);
        ((AppCompatButton) view).setBackgroundResource(R.drawable.circel);
        ((AppCompatButton) view).setSupportBackgroundTintList(getContext().getResources().getColorStateList(R.color.red));

        String day = ((android.support.v7.widget.AppCompatButton) view).getText().toString();
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

    private void getSchedule(String day, String month, String year) {
        UrlData urlData = new UrlData();
        try {
            urlData.add(WebService.Schedule.id_user, MainActivity.jsonObject.getString("id"));
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

        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                //setAdapter // refreshIt
                try {
                    agendaJsonArray = new JSONObject(output).getJSONArray("schedule");
                    agendaAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Schedule.getScheduleDataApi, urlData.get());
    }

    @OnClick(R.id.newAgenda)
    public void addSchedule() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.create_appointment);


        Calendar calendar = new GregorianCalendar();

        final TimePicker timeFrom = (TimePicker) dialog.findViewById(R.id.timeFrom);
        timeFrom.setIs24HourView(true);

        final TimePicker timeTo = (TimePicker) dialog.findViewById(R.id.timeTo);
        timeTo.setIs24HourView(true);

        if (calendar.get(Calendar.MINUTE) >= 30) {
            calendar.add(Calendar.HOUR, 1);
            calendar.set(Calendar.MINUTE, 0);
            timeFrom.setCurrentMinute(calendar.get(Calendar.MINUTE));
            timeFrom.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));

            calendar.add(Calendar.HOUR, 1);
            timeTo.setCurrentMinute(calendar.get(Calendar.MINUTE));
            timeTo.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        } else {
            calendar.set(Calendar.MINUTE, 30);
            timeFrom.setCurrentMinute(calendar.get(Calendar.MINUTE));
            timeFrom.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));

            calendar.add(Calendar.HOUR, 1);
            timeTo.setCurrentMinute(calendar.get(Calendar.MINUTE));
            timeTo.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        }
        Button confirm = (Button) dialog.findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
                Calendar calendar = Calendar.getInstance();
                Log.e("Time", timeFrom.getCurrentHour() + " / " + calendar.get(Calendar.HOUR_OF_DAY) + " / " +
                        timeFrom.getCurrentMinute() + " / " + calendar.get(Calendar.MINUTE));
                if ((calendar.get(Calendar.DAY_OF_MONTH) + "").equals(day.getText().toString())) {
                    if (timeFrom.getCurrentHour() < calendar.get(Calendar.HOUR_OF_DAY)) {
                        Toast.makeText(getActivity(), getActivity().getString(R.string.wrongAppoinment), Toast.LENGTH_LONG).show();
                        return;
                    } else if (timeFrom.getCurrentHour() == calendar.get(Calendar.HOUR_OF_DAY) &&
                            timeFrom.getCurrentMinute() < calendar.get(Calendar.MINUTE)) {
                        Toast.makeText(getActivity(), getActivity().getString(R.string.wrongAppoinment), Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                if (((timeFrom.getCurrentHour())
                        > (timeTo.getCurrentHour())) ||
                        !DataAvailable(timeFrom.getCurrentHour(), timeTo.getCurrentHour())) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setMessage(getActivity().getString(R.string.wrongAppoinment)).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
                } else {
                    if (timeFrom.getCurrentHour() == timeTo.getCurrentHour() && (timeTo.getCurrentMinute() - timeFrom.getCurrentMinute()) < 59) {
                        Toast.makeText(getActivity(), getActivity().getString(R.string.min20), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    UrlData urlData = new UrlData();
                    try {
                        urlData.add(WebService.Schedule.id_user, MainActivity.jsonObject.getString("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    urlData.add(WebService.Schedule.day, addZeroToString(day.getText().toString()));
                    urlData.add(WebService.Schedule.month, addZeroToString((((currentDate.get(Calendar.MONTH)) + 1) + "")));
                    urlData.add(WebService.Schedule.year, ((currentDate.get(Calendar.YEAR)) + ""));
                    urlData.add(WebService.Schedule.from_hour, addZeroToString(timeFrom.getCurrentHour() + ""));
                    urlData.add(WebService.Schedule.from_minutes, addZeroToString(timeFrom.getCurrentMinute() + ""));
                    urlData.add(WebService.Schedule.to_hour, addZeroToString(timeTo.getCurrentHour() + ""));
                    urlData.add(WebService.Schedule.to_minutes, addZeroToString(timeTo.getCurrentMinute() + ""));

                    urlData.add("type", "doctor");


                    new GetData(new GetData.AsyncResponse() {
                        @Override
                        public void processFinish(String output) {
                            //check if true
                            dayClick(day);
                        }
                    }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Schedule.addScheduleApi, urlData.get());
                }
            }

            private boolean DataAvailable(Integer currentHour, Integer currentHour1) {
                boolean valid = true;
                //check if data is available

                for (int i = 0; i < agendaJsonArray.length(); i++) {
                    try {
                        JSONObject agenda = agendaJsonArray.getJSONObject(i);
                        if (currentHour >= Integer.parseInt(agenda.getString(WebService.Schedule.from_hour))
                                && currentHour <= Integer.parseInt(agenda.getString(WebService.Schedule.to_hour))) {
                            valid = false;
                            break;
                        } else if (currentHour1 >= Integer.parseInt(agenda.getString(WebService.Schedule.from_hour))
                                && currentHour1 <= Integer.parseInt(agenda.getString(WebService.Schedule.to_hour))) {
                            valid = false;
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return valid;
            }
        });
        dialog.show();
    }


}



