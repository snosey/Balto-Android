package com.example.snosey.balto.login.create_account;

import android.content.Context;
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
import com.example.snosey.balto.login.NewAccountObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Snosey on 1/31/2018.
 */

public class Name extends Fragment {
    @InjectView(R.id.firstName)
    EditText firstName;
    @InjectView(R.id.lastName)
    EditText lastName;
    @InjectView(R.id.next)
    ImageButton next;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_account_name, container, false);
        next = (ImageButton) view.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firstName.getText().length() == 0 || lastName.getText().length() == 0)
                    Toast.makeText(getActivity(), getActivity().getString(R.string.ask_name), Toast.LENGTH_SHORT).show();
                else {
                    NewAccountObject accountObject = new NewAccountObject();
                    accountObject.firstName = firstName.getText().toString();
                    accountObject.lastName = lastName.getText().toString();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("object", accountObject);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    com.example.snosey.balto.login.Gender fragment = new com.example.snosey.balto.login.Gender();
                    fragment.setArguments(bundle);
                    ft.add(R.id.fragment, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                    View keyboard = getActivity().getCurrentFocus();
                    if (keyboard != null) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(keyboard.getWindowToken(), 0);
                    }
                }
            }
        });

        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

}
