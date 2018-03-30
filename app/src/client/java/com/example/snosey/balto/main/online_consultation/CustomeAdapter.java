package com.example.snosey.balto.main.online_consultation;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.snosey.balto.MainActivity;
import com.example.snosey.balto.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pc on 10/7/2017.
 */
public class CustomeAdapter extends BaseAdapter {
    String colName, id;
    Context context;
    LayoutInflater inflter;
    JSONArray jsonArray;

    public CustomeAdapter(Context applicationContext, JSONArray jsonArray, String name, String id) {


        String any = "{\"" + name + "\": \""+applicationContext.getString(R.string.Both)+"\" , \"" + id + "\": \"-1\"}";
        try {
            JSONObject jsonObject = new JSONObject(any);
            for (int i = jsonArray.length() - 1; i >= 0; i--) {
                jsonArray.put(i + 1, jsonArray.getJSONObject(i));
            }
            jsonArray.put(0, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
        view = inflter.inflate(R.layout.spinner_text, null);
        TextView textView = (TextView) view.findViewById(R.id.text);
        try {
            textView.setText(jsonArray.getJSONObject(i).getString(colName));
            textView.setTag(jsonArray.getJSONObject(i).getString(id));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }
}
