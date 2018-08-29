package com.example.snosey.balto.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.snosey.balto.R;
import com.example.snosey.balto.login.create_account.Email;

import java.util.Locale;

/**
 * Created by Snosey on 1/31/2018.
 */

public class DoctorType extends Fragment {
    ImageButton next;
    Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_account_doctor_type, container, false);
        next = (ImageButton) view.findViewById(R.id.next);
        spinner = (Spinner) view.findViewById(R.id.spinner);

        {

            final AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            builderSingle.setTitle(getActivity().getString(R.string.typeOfService));

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity()
                    , R.layout.spinner_text);

            if (RegistrationActivity.sharedPreferences.getString("lang", "en").equals("en")) {
                arrayAdapter.add("Home Visit");
                arrayAdapter.add("Online Consultation");
                arrayAdapter.add("Both");
            } else {
                arrayAdapter.add("زياره منزليه");
                arrayAdapter.add("كشف عبر الانترنت");
                arrayAdapter.add("الاثنين معا");
            }

            spinner.setAdapter(arrayAdapter);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    {
                        int which = spinner.getSelectedItemPosition();
                        final Bundle bundle = new Bundle();
                        final NewAccountObject accountObject = (NewAccountObject) getArguments().getSerializable("object");
                        accountObject.kindDoctor = (which + 1) + "";
                        bundle.remove("object");
                        bundle.putSerializable("object", accountObject);
                        bundle.putString("json", getArguments().getString("json"));
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        final FragmentTransaction ft = fm.beginTransaction();

                        if (which == 1) {
                            Email fragment = new Email();
                            fragment.setArguments(bundle);
                            ft.add(R.id.fragment, fragment);
                            ft.addToBackStack(null);
                            ft.commit();

                        } else {
                            ChooseLocation fragment = new ChooseLocation();
                            fragment.setArguments(bundle);
                            ft.add(R.id.fragment, fragment);
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                    }
                }
            });

            return view;
        }
    }
}
