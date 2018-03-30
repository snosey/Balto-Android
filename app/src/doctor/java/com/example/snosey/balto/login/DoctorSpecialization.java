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
import com.example.snosey.balto.login.create_account.Email;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by Snosey on 1/31/2018.
 */

public class DoctorSpecialization extends Fragment {
    ImageButton next;
    Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_account_doctor_specilization, container, false);
        next = (ImageButton) view.findViewById(R.id.next);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        JSONObject jsonObject = null;
        try {
            final String output = getArguments().getString("json");
            jsonObject = new JSONObject(output);
            String sub = "";
            if (Locale.getDefault().getLanguage().equals("ar"))
                sub = "sub_ar";
            else
                sub = "sub_en";
            final CustomeAdapterNormal adapterNormal = new CustomeAdapterNormal(getActivity(), jsonObject.getJSONArray("category").getJSONObject(0).getJSONArray(sub), "name", "id");
            spinner.setAdapter(adapterNormal);
            final JSONObject finalJsonObject = jsonObject;
            final String finalSub = sub;
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Bundle bundle = new Bundle();
                    final NewAccountObject accountObject = (NewAccountObject) getArguments().getSerializable("object");
                    TextView special = (TextView) spinner.getSelectedView();
                    try {
                        accountObject.id_sub.clear();
                        accountObject.id_sub.add(special.getTag().toString());
                        accountObject.kindDoctor = finalJsonObject.getJSONArray("category").
                                getJSONObject(0).getJSONArray(finalSub).
                                getJSONObject(spinner.getSelectedItemPosition()).
                                getJSONArray("doctorkind").getJSONObject(0).
                                getString("id_doctor_kind");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    bundle.putString("json", output);
                    bundle.putSerializable("object", accountObject);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    final FragmentTransaction ft = fm.beginTransaction();

                    if (accountObject.kindDoctor.equals("1")) {
                        ChooseLocation fragment = new ChooseLocation();
                        fragment.setArguments(bundle);
                        ft.add(R.id.fragment, fragment);
                        ft.addToBackStack(null);
                        ft.commit();

                    } else if (accountObject.kindDoctor.equals("2")) {
                        Email fragment = new Email();
                        fragment.setArguments(bundle);
                        ft.add(R.id.fragment, fragment);
                        ft.addToBackStack(null);
                        ft.commit();
                    } else {
                        DoctorType fragment = new DoctorType();
                        fragment.setArguments(bundle);
                        ft.add(R.id.fragment, fragment);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }
}
