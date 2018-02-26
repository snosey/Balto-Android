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
import android.widget.Toast;

import com.example.snosey.balto.MainActivity;
import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.login.ForgetPassword;
import com.example.snosey.balto.login.NewAccountObject;
import com.example.snosey.balto.login.create_account.Phone;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

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

        sharedPreferences = getActivity().getSharedPreferences("login_client", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (getArguments() != null && getArguments().containsKey("auto")) {
            String type = sharedPreferences.getString("type", "");
            if (type.equals("facebook"))
                checkFacebook();
            else if (type.equals("email"))
                check_Username_Password(sharedPreferences.getString("email", ""), sharedPreferences.getString("password", ""));
            else
                getActivity().onBackPressed();
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
        urlData.add("type", "client");
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

    @OnClick(R.id.signin_facebook)
    public void onViewClicked() {
        checkFacebook();
    }

    private void checkFacebook() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        final Profile profile = Profile.getCurrentProfile();

                        UrlData urlData = new UrlData();
                        urlData.add("type", "client");
                        urlData.add(WebService.Login.id_provider, profile.getId());
                        urlData.add(WebService.Login.fcm_token, FirebaseInstanceId.getInstance().getToken());
                        new GetData(new GetData.AsyncResponse() {
                            @Override
                            public void processFinish(String output) {
                                if (output.contains("false")) {
                                    newAccount(profile, loginResult);
                                } else {
                                    try {
                                        editor.putString("type", "facebook");
                                        editor.commit();

                                        startNewActivity(new JSONObject(output).getJSONObject("user").toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, getActivity(), true).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, WebService.Login.loginApi, urlData.get());

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getActivity(), "You canceled login with facebook!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        try {
            if (callbackManager.onActivityResult(requestCode, resultCode, data))
                return;
        } catch (Exception e) {

        }
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

    private void newAccount(final Profile profile, LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {

                        NewAccountObject accountObject = new NewAccountObject();
                        accountObject.firstName = (profile.getFirstName());
                        accountObject.lastName = (profile.getLastName());

                        try {
                            if (object.getString("gender").equals("male") || object.getString("gender").equals("ذكر"))
                                accountObject.gender = ("1");
                            else
                                accountObject.gender = ("2");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        accountObject.id_provider = (profile.getId());
                        accountObject.provider_kind = ("facebook");
                        accountObject.logo = ("https://graph.facebook.com/" + profile.getId() + "/picture?type=large");
                        accountObject.type = (WebService.SignUp.typeClient);

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("object", accountObject);
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        Phone fragment = new Phone();
                        fragment.setArguments(bundle);
                        ft.add(R.id.fragment, fragment);
                        ft.addToBackStack(null);
                        ft.commit();


                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "gender");
        request.setParameters(parameters);
        request.executeAsync();

    }

}
