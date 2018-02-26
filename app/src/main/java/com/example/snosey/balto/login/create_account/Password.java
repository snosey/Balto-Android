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

public class Password extends Fragment {
    ImageButton next;
    @InjectView(R.id.password)
    EditText password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_account_password, container, false);
        ButterKnife.inject(this, view);
        next = (ImageButton) view.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password.getText().toString().length() == 0)
                    Toast.makeText(getActivity(), getActivity().getString(R.string.create_password), Toast.LENGTH_SHORT).show();

                else {
                    View keyboard = getActivity().getCurrentFocus();
                    if (keyboard != null) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(keyboard.getWindowToken(), 0);
                    }
                    NewAccountObject accountObject = (NewAccountObject) getArguments().getSerializable("object");
                    accountObject.password = password.getText().toString();
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
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}