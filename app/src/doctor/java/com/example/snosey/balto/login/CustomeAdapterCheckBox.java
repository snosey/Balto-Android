package com.example.snosey.balto.login;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.snosey.balto.R;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by pc on 10/7/2017.
 */
class CustomeAdapterCheckBox extends BaseAdapter {
    String colName, id;
    Context context;
    LayoutInflater inflter;
    JSONArray jsonArray;
    String spinnerName;
    private boolean isFromView = false;

    public CustomeAdapterCheckBox(Context applicationContext, JSONArray jsonArray, String name, String id) {


        this.jsonArray = jsonArray;
        this.context = applicationContext;
        this.colName = name;
        this.id = id;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.spinner_text_checkbox, null);
        TextView textView = (TextView) view.findViewById(R.id.text);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        try {
            textView.setText(jsonArray.getJSONObject(i).getString(colName));
            textView.setTag(jsonArray.getJSONObject(i).getString(id));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        checkBox.setTag(i);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int getPosition = (Integer) buttonView.getTag();


            }
        });
        return view;
    }
}
