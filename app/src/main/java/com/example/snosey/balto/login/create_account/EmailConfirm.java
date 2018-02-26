package com.example.snosey.balto.login.create_account;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.login.NewAccountObject;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Snosey on 1/31/2018.
 */

public class EmailConfirm extends Fragment {

    @InjectView(R.id.code)
    EditText code;
    @InjectView(R.id.resend)
    Button resend;
    String codeConfirm;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_account_email_confirm, container, false);
        ButterKnife.inject(this, view);
        codeConfirm=getArguments().getString("code");
        code.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 4) {
                    if (codeConfirm.equals(s.toString())) {
                        NewAccountObject accountObject = (NewAccountObject) getArguments().getSerializable("object");
                        Bundle bundle = new Bundle();
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
            }
        });
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resend();
            }
        });

        return view;
    }

    private void resend() {
        UrlData urlData = new UrlData();
        urlData.add(WebService.SignUp.email, getArguments().getString("email"));
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                try {
                    codeConfirm=new JSONObject(output).getString("code");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, WebService.SignUp.checkEmailApi, urlData.get());

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
