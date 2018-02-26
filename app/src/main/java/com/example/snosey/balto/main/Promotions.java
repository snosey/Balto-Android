package com.example.snosey.balto.main;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Snosey on 2/12/2018.
 */

public class Promotions extends Fragment {
    @InjectView(R.id.promotionsRV)
    RecyclerView promotionsRV;
    PromotionsAdapter promotionsAdapter;
    List<JSONObject> jsonObjects;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.promotions, container, false);
        ButterKnife.inject(this, view);


        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.back)).setVisibility(View.VISIBLE);
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.menu)).setVisibility(View.GONE);
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.right_icon)).setVisibility(View.GONE);

        jsonObjects = new ArrayList<JSONObject>();
        promotionsAdapter = new PromotionsAdapter();
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        promotionsRV.setLayoutManager(layoutManager);
        promotionsRV.setAdapter(promotionsAdapter);

        UrlData urlData = new UrlData();
        urlData.add(WebService.PromoCode.id_user, getArguments().getString(WebService.PromoCode.id_user));
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (output.contains("true")) {
                    try {
                        JSONObject jsonObject = new JSONObject(output);
                        JSONArray jsonArray = jsonObject.getJSONArray("coupons");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObjects.add(jsonArray.getJSONObject(i));
                        }
                        if (jsonObjects.size() == 0) {
                            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                            alertDialog.setMessage(getString(R.string.noPromo));
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.share),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            ((LinearLayout) getActivity().getWindow().getDecorView().findViewById(R.id.share)).callOnClick();
                                            getActivity().onBackPressed();
                                        }
                                    });
                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.back),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            getActivity().onBackPressed();
                                        }
                                    });
                            alertDialog.show();
                        }
                        promotionsAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.PromoCode.getPromoCodeUserApi, urlData.get());


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        Button copy;
        TextView title, code;


        public MyViewHolder(View v) {
            super(v);
            copy = (Button) v.findViewById(R.id.copy);
            title = (TextView) v.findViewById(R.id.title);
            code = (TextView) v.findViewById(R.id.code);
        }
    }

    private class PromotionsAdapter extends RecyclerView.Adapter<Promotions.MyViewHolder> {
        @Override
        public Promotions.MyViewHolder onCreateViewHolder(ViewGroup parent, final int position) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.promotion_row, parent, false);
            return new Promotions.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final Promotions.MyViewHolder holder, final int position) {
            final JSONObject jsonObject = jsonObjects.get(position);
            try {
                holder.title.setText(jsonObject.getString("coupon_text"));
                holder.code.setText(jsonObject.getString("code"));
                holder.copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = null;
                        try {
                            clip = ClipData.newPlainText(getActivity().getString(android.R.string.copy), jsonObject.getString("code"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getActivity(), getActivity().getString(android.R.string.copy), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return (int) jsonObjects.size();
        }
    }


}
