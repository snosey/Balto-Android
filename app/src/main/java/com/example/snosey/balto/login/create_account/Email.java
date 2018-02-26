package com.example.snosey.balto.login.create_account;

import android.content.Context;
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

import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.login.NewAccountObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Snosey on 1/31/2018.
 */

public class Email extends Fragment {
    ImageButton next;
    @InjectView(R.id.email)
    EditText email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_account_email, container, false);
        ButterKnife.inject(this, view);
        next = (ImageButton) view.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (email.getText().length() == 0)
                    Toast.makeText(getActivity(), getActivity().getString(R.string.ask_email), Toast.LENGTH_SHORT).show();
                else if (!isEmailValid(email.getText().toString())) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.invalidEmail), Toast.LENGTH_SHORT).show();
                } else {
                    View keyboard = getActivity().getCurrentFocus();
                    if (keyboard != null) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(keyboard.getWindowToken(), 0);
                    }

                    UrlData urlData = new UrlData();
                    urlData.add(WebService.SignUp.email, email.getText().toString());
                    new GetData(new GetData.AsyncResponse() {
                        @Override
                        public void processFinish(String output) {
                            if (output.contains("false")) {
                                Toast.makeText(getActivity(), getActivity().getString(R.string.emailIsExist), Toast.LENGTH_SHORT).show();
                            } else {
                                Bundle bundle = new Bundle();
                                try {
                                    JSONObject jsonObject = new JSONObject(output);
                                  //  bundle.putString("code", jsonObject.getString("code"));
                               //     bundle.putString("email", email.getText().toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                NewAccountObject accountObject = (NewAccountObject) getArguments().getSerializable("object");
                                accountObject.email = email.getText().toString();
                                bundle.putSerializable("object", accountObject);
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                Password fragment = new Password();
                                fragment.setArguments(bundle);
                                ft.add(R.id.fragment, fragment);
                                ft.addToBackStack(null);
                                ft.commit();

                            }
                        }
                    }, getActivity(), true).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, WebService.SignUp.checkEmailApi, urlData.get());

                }
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    private static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}