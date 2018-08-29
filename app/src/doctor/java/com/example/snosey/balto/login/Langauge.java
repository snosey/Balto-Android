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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Snosey on 1/31/2018.
 */

public class Langauge extends Fragment {
    ImageButton next;
    Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_account_langauge, container, false);
        next = (ImageButton) view.findViewById(R.id.next);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        JSONObject jsonObject = null;
        try {
            final String output = getArguments().getString("json");
            jsonObject = new JSONObject(output);
            spinner.setAdapter(new CustomeAdapterNormal(getActivity(), jsonObject.getJSONArray("language"), "name", "id"));
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    NewAccountObject accountObject = (NewAccountObject) getArguments().getSerializable("object");
                    com.example.snosey.balto.Support.CustomTextView langId = (com.example.snosey.balto.Support.CustomTextView) spinner.getSelectedView();
                    accountObject.lang = langId.getTag().toString();
                    bundle.putString("json", output);
                    bundle.putSerializable("object", accountObject);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    DoctorKind fragment = new DoctorKind();
                    fragment.setArguments(bundle);
                    ft.add(R.id.fragment, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }
}
