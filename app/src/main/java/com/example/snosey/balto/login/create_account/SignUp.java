package com.example.snosey.balto.login.create_account;

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
import android.widget.Toast;

import com.example.snosey.balto.BuildConfig;
import com.example.snosey.balto.MainActivity;
import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.login.NewAccountObject;
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

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Snosey on 1/31/2018.
 */

public class SignUp extends Fragment {

    private CallbackManager callbackManager;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_up, container, false);
        ButterKnife.inject(this, view);

        sharedPreferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
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


    @OnClick(R.id.signup_facebook)
    public void onViewClicked() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        final Profile profile = Profile.getCurrentProfile();

                        UrlData urlData = new UrlData();
                        if (BuildConfig.APPLICATION_ID.contains("doctor"))
                            urlData.add(WebService.SignUp.type, WebService.Booking.doctor);
                        else
                            urlData.add(WebService.SignUp.type, WebService.Booking.client);
                        urlData.add(WebService.Login.id_provider, profile.getId());
                        urlData.add(WebService.Login.fcm_token, FirebaseInstanceId.getInstance().getToken());
                        new GetData(new GetData.AsyncResponse() {
                            @Override
                            public void processFinish(String output) {
                                if (output.contains("false")) {
                                    newAccount(profile, loginResult);
                                } else
                                    editor.putString("type", "facebook");
                                editor.commit();

                                try {
                                    startNewActivity(new JSONObject(output).getJSONObject("user").toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
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

    private void startNewActivity(String json) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("userData", json);
        startActivity(intent);
        getActivity().finish();
    }


}
