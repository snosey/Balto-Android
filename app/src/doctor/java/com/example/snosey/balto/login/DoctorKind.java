package com.example.snosey.balto.login;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by Snosey on 1/31/2018.
 */

public class DoctorKind extends Fragment {
    ImageButton next;
    Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_account_doctor_kind, container, false);
        next = (ImageButton) view.findViewById(R.id.next);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        JSONObject jsonObject = null;
        try {
            final String output = getArguments().getString("json");
            jsonObject = new JSONObject(output);
            spinner.setAdapter(new CustomeAdapterNormal(getActivity(), jsonObject.getJSONArray("category"), "name", "id"));
            final JSONObject finalJsonObject = jsonObject;
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    NewAccountObject accountObject = (NewAccountObject) getArguments().getSerializable("object");
                    com.example.snosey.balto.Support.CustomTextView kindId = (com.example.snosey.balto.Support.CustomTextView) spinner.getSelectedView();
                    if (kindId.getTag().toString().equals("1")) {
                        bundle.putString("json", output);
                        bundle.putSerializable("object", accountObject);
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        DoctorSpecialization fragment = new DoctorSpecialization();
                        fragment.setArguments(bundle);
                        ft.add(R.id.fragment, fragment);
                        ft.addToBackStack(null);
                        ft.commit();
                    } else {
                        accountObject.kindDoctor = "1";
                        try {
                            String sub = "";
                            if (RegistrationActivity.sharedPreferences.getString("lang", "en").equals("ar"))
                                sub = "sub_ar";
                            else
                                sub = "sub_en";
                            JSONArray jsonArray =
                                    finalJsonObject.getJSONArray("category").
                                            getJSONObject(spinner.getSelectedItemPosition()).getJSONArray(sub);
                            accountObject.id_sub.clear();
                            for (int i = 0; i < jsonArray.length(); i++)
                                accountObject.id_sub.add(jsonArray.getJSONObject(i).getString("id"));

                            bundle.putString("json", output);
                            bundle.putSerializable("object", accountObject);
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            ChooseLocation fragment = new ChooseLocation();
                            fragment.setArguments(bundle);
                            ft.add(R.id.fragment, fragment);
                            ft.addToBackStack(null);
                            ft.commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }
}
