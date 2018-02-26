package com.example.snosey.balto.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.facebook.CallbackManager;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.example.snosey.balto.MainActivity;

/**
 * Created by Snosey on 1/31/2018.
 */

public class SignIn extends Fragment {

    @InjectView(R.id.email)
    EditText email;
    @InjectView(R.id.password)
    EditText password;
    @InjectView(R.id.next)
    ImageButton next;
    private CallbackManager callbackManager;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_in, container, false);
        ButterKnife.inject(this, view);

        sharedPreferences = getActivity().getSharedPreferences("login_doctor", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (getArguments() != null && getArguments().containsKey("auto")) {
            String type = sharedPreferences.getString("type", "");
            if (type.equals("email"))
                check_Username_Password(sharedPreferences.getString("email", ""), sharedPreferences.getString("password", ""));
            else {
                ((ImageView) getActivity().findViewById(R.id.background)).setVisibility(View.GONE);
            }
        }
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().length() == 0)
                    Toast.makeText(getActivity(), getActivity().getString(R.string.ask_email), Toast.LENGTH_SHORT).show();
                else if (password.getText().length() == 0)
                    Toast.makeText(getActivity(), getActivity().getString(R.string.ask_password), Toast.LENGTH_SHORT).show();
                else if (!isEmailValid(email.getText().toString())) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.invalidEmail), Toast.LENGTH_SHORT).show();
                } else {
                    View keyboard = getActivity().getCurrentFocus();
                    if (keyboard != null) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(keyboard.getWindowToken(), 0);
                    }
                    check_Username_Password(email.getText().toString(), password.getText().toString());
                }
            }

        });
        return view;
    }

    private void check_Username_Password(final String email, final String password) {
        UrlData urlData = new UrlData();
        urlData.add(WebService.Login.email, email);
        urlData.add("type","doctor");
        urlData.add(WebService.Login.password, password);
        urlData.add(WebService.Login.fcm_token, FirebaseInstanceId.getInstance().getToken());

        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (output.contains("false")) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.wrongEmailOrPassword), Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        editor.putString("email", email);
                        editor.putString("password", password);
                        editor.putString("type", "email");
                        editor.commit();

                        startNewActivity(new JSONObject(output).getJSONObject("user").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, WebService.Login.loginApi, urlData.get());


    }

    private void startNewActivity(String json) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("userData", json);
        startActivity(intent);
        getActivity().finish();
    }

    private static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.forgetPassword)
    public void onForgetPasswordClicked() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ForgetPassword fragment = new ForgetPassword();
        ft.add(R.id.fragment, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

}
