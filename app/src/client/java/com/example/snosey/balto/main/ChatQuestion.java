package com.example.snosey.balto.main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.Support.webservice.WebServiceVolley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.example.snosey.balto.Support.webservice.WebService.OnlineConsult.sendMessagesApi;

public class ChatQuestion extends Fragment {


    @InjectView(R.id.hasasyaGroup)
    RadioGroup hasasyaGroup;
    @InjectView(R.id.hmlGroup)
    RadioGroup hmlGroup;
    @InjectView(R.id.howFarHaml)
    AppCompatEditText howFarHaml;
    @InjectView(R.id.mozmnaGroup)
    RadioGroup mozmnaGroup;
    @InjectView(R.id.mozmnaText)
    AppCompatEditText mozmnaText;
    @InjectView(R.id.adwyaGroup)
    RadioGroup adwyaGroup;
    @InjectView(R.id.adwyaText)
    AppCompatEditText adwyaText;
    @InjectView(R.id.consultation)
    AppCompatEditText consultation;
    @InjectView(R.id.start)
    Button start;
    @InjectView(R.id.ageSpinner)
    AppCompatSpinner ageSpinner;
    @InjectView(R.id.genderSpinner)
    AppCompatSpinner genderSpinner;
    @InjectView(R.id.hmlGroupYes)
    RadioButton hmlGroupYes;
    @InjectView(R.id.hmlGroupNo)
    RadioButton hmlGroupNo;
    @InjectView(R.id.hamlLayout)
    LinearLayout hamlLayout;
    @InjectView(R.id.hasasyaGroupYes)
    RadioButton hasasyaGroupYes;
    @InjectView(R.id.hasasyaGroupNo)
    RadioButton hasasyaGroupNo;
    @InjectView(R.id.mozmnaGroupYes)
    RadioButton mozmnaGroupYes;
    @InjectView(R.id.mozmnaGroupNo)
    RadioButton mozmnaGroupNo;
    @InjectView(R.id.adwyaGroupYes)
    RadioButton adwyaGroupYes;
    @InjectView(R.id.adwyaGroupNo)
    RadioButton adwyaGroupNo;
    private String chatId, doctorId, patientId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_questions, container, false);


        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.back)).setVisibility(View.VISIBLE);
        ((RelativeLayout) getActivity().getWindow().getDecorView().findViewById(R.id.menuHome)).setVisibility(View.GONE);
        ButterKnife.inject(this, view);
        mozmnaGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (R.id.mozmnaGroupYes == checkedId) {
                    mozmnaText.setVisibility(View.VISIBLE);
                } else {
                    mozmnaText.setVisibility(View.GONE);
                }
            }
        });
        adwyaGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.adwyaGroupYes) {
                    adwyaText.setVisibility(View.VISIBLE);
                } else {
                    adwyaText.setVisibility(View.GONE);
                }
            }
        });
        hmlGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.hmlGroupYes) {
                    howFarHaml.setVisibility(View.VISIBLE);
                } else {
                    howFarHaml.setVisibility(View.GONE);
                }
            }
        });


        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add(getActivity().getString(R.string.male));
        spinnerArray.add(getActivity().getString(R.string.female));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.spinner_text,
                spinnerArray
        );
        genderSpinner.setAdapter(adapter);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (genderSpinner.getSelectedItemPosition() == 0) {
                    hamlLayout.setVisibility(View.GONE);
                } else {
                    hamlLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        setAgeSpinner();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    void openChatRoom() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("id", chatId);
        ChatRoom fragment = new ChatRoom();
        FragmentTransaction ft = fm.beginTransaction();
        fragment.setArguments(bundle);
        ft.replace(R.id.fragment, fragment, "ChatRoom");
        //ft.addToBackStack("Chat");
        ft.commit();
    }

    @OnClick(R.id.start)
    public void onViewClicked() {
        final String msg = getMsg();
        if (msg.equals("")) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.emptyField), Toast.LENGTH_SHORT).show();
            return;
        }
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                JSONObject jsonObject = new JSONObject(output);
                if (jsonObject.getBoolean("status")) {
                    chatId = jsonObject.getJSONObject("chat").getString("id");
                    doctorId = jsonObject.getJSONObject("chat").getString("id_doctor");
                    patientId = jsonObject.getJSONObject("chat").getString("id_patient");
                    sendConsultation(msg, patientId);

                } else {
                    Toast.makeText(getActivity(), "Error, please try again", Toast.LENGTH_SHORT).show();
                }
                Log.e("chatCreated", output);
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.OnlineConsult.createChatApi, getArguments().getString("url"));

    }

    private void sendConsultation(String msg, final String userId) {


        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
            jsonObject.put("id_chat", chatId);
            jsonObject.put("message", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new WebServiceVolley(getActivity(), Request.Method.POST, sendMessagesApi, true,
                "Error, please try again", jsonObject, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (userId.equals(patientId)) {
                    sendNotification(response);
                    sendConsultation("شكرا لمراسلتنا, سوف يجيب الطبيب على استفسارك في خلال 24 ساعه", doctorId);
                } else
                    openChatRoom();
            }

        }).sendRequest();
    }

    private String getMsg() {
        String msg = "";
        msg += answeredQuestion(getActivity().getString(R.string.age), ageSpinner.getSelectedItem().toString());
        msg += answeredQuestion(getActivity().getString(R.string.chooseGender), genderSpinner.getSelectedItem().toString());
        if (genderSpinner.getSelectedItemPosition() == 1)
            if (hmlGroupYes.isChecked()) {
                if (howFarHaml.getText().toString().trim().length() == 0) {
                    howFarHaml.setError(getActivity().getString(R.string.emptyField));
                    return "";
                } else {
                    msg += answeredQuestion(getActivity().getString(R.string.hml), howFarHaml.getText().toString());
                }
            } else {
                msg += answeredQuestion(getActivity().getString(R.string.hml), getActivity().getString(R.string.no));
            }

        if (hasasyaGroupYes.isChecked()) {
            msg += answeredQuestion(getActivity().getString(R.string.hasasya), getActivity().getString(R.string.yes));
        } else
            msg += answeredQuestion(getActivity().getString(R.string.hasasya), getActivity().getString(R.string.no));

        if (adwyaGroupYes.isChecked()) {
            if (adwyaText.getText().toString().trim().length() == 0) {
                adwyaText.setError(getActivity().getString(R.string.emptyField));
                return "";
            } else {
                msg += answeredQuestion(getActivity().getString(R.string.adwya), adwyaText.getText().toString());
            }
        } else
            msg += answeredQuestion(getActivity().getString(R.string.adwya), getActivity().getString(R.string.no));


        if (mozmnaGroupYes.isChecked()) {
            if (mozmnaText.getText().toString().trim().length() == 0) {
                mozmnaText.setError(getActivity().getString(R.string.emptyField));
                return "";
            } else {
                msg += answeredQuestion(getActivity().getString(R.string.mozmna), mozmnaText.getText().toString());
            }
        } else
            msg += answeredQuestion(getActivity().getString(R.string.mozmna), getActivity().getString(R.string.no));
        if (consultation.getText().toString().trim().length() == 0) {
            consultation.setError(getActivity().getString(R.string.emptyField));
            return "";
        } else {
            msg += answeredQuestion(getActivity().getString(R.string.whatIsConsultation), consultation.getText().toString());
        }

        return msg;
    }

    private String answeredQuestion(String question, String answer) {
        String msg = question + "\n" + answer + "\n\n";
        return msg;
    }


    private void setAgeSpinner() {
        List<Integer> spinnerArray = new ArrayList<>();
        for (Integer i = 7; i < 69; i++)
            spinnerArray.add(i);

        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(
                getActivity(),
                R.layout.spinner_text,
                spinnerArray
        );
        ageSpinner.setAdapter(adapter);
    }

    void sendNotification(String response) {
        {
            String fcm_token = getArguments().getString("fcm_token");
            if (fcm_token.equals(""))
                return;

            final UrlData urlData = new UrlData();
            urlData.add(WebService.Notification.reg_id, fcm_token);
            urlData.add(WebService.Notification.data, response);
            urlData.add(WebService.Notification.kind, WebService.Notification.Types.newMsg);
            urlData.add(WebService.Notification.message, " ");
            urlData.add(WebService.Notification.title, getActivity().getString(R.string.newMsg));

            new GetData(new GetData.AsyncResponse() {
                @Override
                public void processFinish(String output) {

                }
            }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Notification.notificationApi, urlData.get());

        }
    }
}