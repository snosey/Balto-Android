package com.example.snosey.balto.main.online_consultation;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.login.RegistrationActivity;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Snosey on 3/8/2018.
 */

public class Main extends Fragment {
    @InjectView(R.id.secondCategory)
    Spinner secondCategory;
    @InjectView(R.id.thirdCategory)
    Spinner thirdCategory;
    @InjectView(R.id.gender)
    Spinner gender;
    @InjectView(R.id.doctorName)
    EditText doctorName;
    @InjectView(R.id.date)
    com.example.snosey.balto.Support.CustomTextView date;
    @InjectView(R.id.clear)
    com.example.snosey.balto.Support.CustomTextView clear;
    @InjectView(R.id.language)
    Spinner language;

    String chooseDay = "", chooseMonth = "", chooseYear = "";

    protected static LinearLayout genderLL;
    protected static LinearLayout languageLL;
    protected static LinearLayout dateLl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.online_consultation_main, container, false);
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.back)).setVisibility(View.VISIBLE);
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.menu)).setVisibility(View.GONE);

        ButterKnife.inject(this, view);

        genderLL = (LinearLayout) view.findViewById(R.id.genderLL);
        dateLl = (LinearLayout) view.findViewById(R.id.dateLl);
        languageLL = (LinearLayout) view.findViewById(R.id.languageLL);

        languageLL.setVisibility(View.GONE);
        dateLl.setVisibility(View.VISIBLE);
        genderLL.setVisibility(View.GONE);
        setData();
        thirdCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0)
                    onViewClicked();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return view;
    }


    private void setData() {
        UrlData urlData = new UrlData();
        urlData.add(WebService.OnlineConsult.type, RegistrationActivity.sharedPreferences.getString("lang", "en"));

        new GetData(new GetData.AsyncResponse() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void processFinish(String output) {
                if (output.length() != 0) {
                    try {
                        final JSONObject jsonObject = new JSONObject(output);
                        final JSONArray subCategory = jsonObject.getJSONArray("subCategory");
                        JSONArray gender = (jsonObject.getJSONArray("gender"));
                        JSONArray spoken_language = (jsonObject.getJSONArray("spoken_language"));

                        Main.this.secondCategory.setAdapter(new CustomeAdapter(getActivity(), subCategory, "name", "id"));
                        Main.this.secondCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                com.example.snosey.balto.Support.CustomTextView selected = (com.example.snosey.balto.Support.CustomTextView) view;
                                if (i == 0) {
                                    Main.this.thirdCategory.setAdapter(null);
                                    return;
                                }
                                try {
                                    JSONArray thirdCategory = subCategory.getJSONObject(i - 1).getJSONArray("Third");
                                    Main.this.thirdCategory.setAdapter(new CustomeAdapter(getActivity(), thirdCategory, "name", "id"));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        Main.this.gender.setAdapter(new CustomeAdapter(getActivity(), gender, "name", "id"));
                        spoken_language.remove(2);
                        Main.this.language.setAdapter(new CustomeAdapter(getActivity(), spoken_language, "name", "id"));
                        date.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Calendar now = Calendar.getInstance();
                                DatePickerDialog dpd = DatePickerDialog.newInstance(
                                        new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                                chooseDay = addZero(dayOfMonth + "");
                                                chooseMonth = addZero(monthOfYear + "");
                                                chooseYear = addZero(year + "");
                                                date.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
                                                date.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
                                                clear.setVisibility(View.VISIBLE);
                                                clear.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        chooseDay="";
                                                        chooseMonth="";
                                                        chooseYear="";
                                                        date.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                                                        date.setText(getActivity().getString(R.string.chooseDate));
                                                        clear.setVisibility(View.GONE);
                                                    }
                                                });
                                            }
                                        },
                                        now.get(Calendar.YEAR),
                                        now.get(Calendar.MONTH),
                                        now.get(Calendar.DAY_OF_MONTH)
                                );
                                dpd.setMinDate(now);
                                Calendar nextWeek = Calendar.getInstance();
                                nextWeek.add(Calendar.DAY_OF_MONTH, 7);
                                dpd.setMaxDate(nextWeek);
                                dpd.show(getActivity().getFragmentManager(), "");
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.OnlineConsult.getFilterDataApi, urlData.get());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getView() == null) {
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (genderLL.isShown()) {
                        onViewClicked();
                    } else getActivity().onBackPressed();
                    return true;
                }
                return false;
            }
        });
    }


    @OnClick(R.id.next)
    public void onViewClicked() {

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        UrlData urlData = new UrlData();
        try {
            urlData.add(WebService.OnlineConsult.type, RegistrationActivity.sharedPreferences.getString("lang", "en"));

            if (doctorName.getText().toString().length() != 0)
                urlData.add(WebService.OnlineConsult.name, doctorName.getText().toString());

            if (!chooseDay.equals("")) {
                urlData.add(WebService.OnlineConsult.day, chooseDay);
                urlData.add(WebService.OnlineConsult.month, chooseMonth);
                urlData.add(WebService.OnlineConsult.year, chooseYear);
            }
            if (secondCategory.getSelectedItemPosition() != 0)
                urlData.add(WebService.OnlineConsult.id_sub, ((com.example.snosey.balto.Support.CustomTextView) secondCategory.getSelectedView()).getTag().toString());


            if (gender.getSelectedItemPosition() != 0)
                urlData.add(WebService.OnlineConsult.id_gender, ((com.example.snosey.balto.Support.CustomTextView) gender.getSelectedView()).getTag().toString());

            if (language.getSelectedItemPosition() != 0)
                urlData.add(WebService.OnlineConsult.id_language, ((com.example.snosey.balto.Support.CustomTextView) language.getSelectedView()).getTag().toString());
        } catch (Exception e) {
            return;
        }
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                try {
                    JSONArray jsonArray = new JSONObject(output).getJSONArray("users");
                    if (jsonArray.length() == 0)
                        Toast.makeText(getActivity(), getActivity().getString(R.string.noProfissional), Toast.LENGTH_LONG).show();
                    else {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        Bundle bundle = new Bundle();
                        bundle.putString("json", jsonArray.toString());
                        DoctorList fragment = new DoctorList();
                        FragmentTransaction ft = fm.beginTransaction();
                        fragment.setArguments(bundle);
                        ft.add(R.id.fragment, fragment, "DoctorList");
                        ft.addToBackStack("DoctorList");
                        ft.commit();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                languageLL.setVisibility(View.GONE);
                                genderLL.setVisibility(View.GONE);
                            }
                        }, 300);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.OnlineConsult.getDoctorsApi, urlData.get());
    }

    private String addZero(String number) {
        if (number.length() == 1) {
            number = "0" + number;
        }
        return number;
    }
}
