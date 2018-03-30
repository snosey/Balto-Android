package com.example.snosey.balto.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snosey.balto.R;
import com.example.snosey.balto.login.create_account.Email;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Snosey on 1/31/2018.
 */

public class ChooseLocation extends Fragment {
    ImageButton next;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_account_location, container, false);
        next = (ImageButton) view.findViewById(R.id.next);
        listView = (ListView) view.findViewById(R.id.list_item);
        JSONObject jsonObject = null;
        try {
            final String output = getArguments().getString("json");
            jsonObject = new JSONObject(output);
            final CustomeAdapterCheckBox customeAdapterCheckBox = new
                    CustomeAdapterCheckBox(getActivity(), jsonObject.getJSONArray("cities"), "name", "id");
            listView.setAdapter(customeAdapterCheckBox);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    NewAccountObject accountObject = (NewAccountObject) getArguments().getSerializable("object");
                    accountObject.city.clear();
                    for (int i = 0; i < listView.getCount(); i++) {
                        View adapterView = listView.getChildAt(i);
                        CheckBox checkBox = (CheckBox) adapterView.findViewById(R.id.checkbox);
                        if (checkBox.isChecked()) {
                            accountObject.city.add(((TextView) adapterView.findViewById(R.id.text)).getTag().toString());
                        }
                    }
                    if (accountObject.city.size() > 0) {
                        Log.e("city", accountObject.city.toString());
                        bundle.putString("json", output);
                        bundle.putSerializable("object", accountObject);
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        Email fragment = new Email();
                        fragment.setArguments(bundle);
                        ft.add(R.id.fragment, fragment);
                        ft.addToBackStack(null);
                        ft.commit();
                    } else
                        Toast.makeText(getActivity(), getActivity().getString(R.string.chooseLocation), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }
}
