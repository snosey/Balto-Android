package com.example.snosey.balto.login;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.login.create_account.CustomeAdapter;
import com.example.snosey.balto.login.create_account.Email;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Snosey on 1/31/2018.
 */

public class Gender extends Fragment {
    ImageButton next;
    Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_account_gender, container, false);
        next = (ImageButton) view.findViewById(R.id.next);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        String kind = "";
        SharedPreferences sharedPreferences;
        sharedPreferences = getActivity().getSharedPreferences("login_client", MODE_PRIVATE);
        if (sharedPreferences.getString("lang", "en").equals("ar"))
            kind = WebService.SignUp.getDataApiAr;
        else kind = WebService.SignUp.getDataApiEn;

        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(final String output) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(output);
                    spinner.setAdapter(new CustomeAdapter(getActivity(), jsonObject.getJSONArray("gender"), "name", "id"));
                    next.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle bundle = new Bundle();
                            NewAccountObject accountObject = (NewAccountObject) getArguments().getSerializable("object");
                            com.example.snosey.balto.Support.CustomTextView gender = (com.example.snosey.balto.Support.CustomTextView) spinner.getSelectedView();
                            accountObject.gender = gender.getTag().toString();
                            bundle.putString("json", output);
                            bundle.putSerializable("object", accountObject);
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            Email fragment = new Email();
                            fragment.setArguments(bundle);
                            ft.add(R.id.fragment, fragment);
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, WebService.SignUp.getDataApi, kind);

        return view;
    }
}
