package com.example.snosey.balto.main;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.snosey.balto.BuildConfig;
import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.CustomTextView;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.login.RegistrationActivity;
import com.example.snosey.balto.login.create_account.CustomeAdapter;
import com.example.snosey.balto.main.home_visit.Main;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.example.snosey.balto.MainActivity.jsonObject;

/**
 * Created by Snosey on 2/12/2018.
 */

public class HomeAndOnline2 extends Fragment {


    @InjectView(R.id.name)
    CustomTextView name;
    @InjectView(R.id.chatting)
    ImageView chat;
    @InjectView(R.id.onlineConsult)
    ImageView onlineConsult;
    @InjectView(R.id.homeVisiting)
    ImageView homeVisiting;
    @InjectView(R.id.shareApp)
    ImageView shareApp;


    private void setData(final Spinner secondCategory, final Spinner thirdCategory) {
        UrlData urlData = new UrlData();
        urlData.add(WebService.OnlineConsult.type, RegistrationActivity.sharedPreferences.getString("lang", "en"));

        new GetData(new GetData.AsyncResponse() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void processFinish(String output) {
                if (output.length() != 0) {
                    try {
                        final JSONObject jsonObject = new JSONObject(output);
                        final JSONArray subCategory = jsonObject.getJSONArray("subCategory");

                        secondCategory.setAdapter(new CustomeAdapter(getActivity(), subCategory, "name", "id"));
                        secondCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                try {
                                    JSONArray thirdCategoryJsonArray = subCategory.getJSONObject(i).getJSONArray("Third");
                                    thirdCategory.setAdapter(new CustomeAdapter(getActivity(), thirdCategoryJsonArray, "name", "id"));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.OnlineConsult.getFilterDataApi, urlData.get());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_main_new, container, false);


        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.back)).setVisibility(View.GONE);
        ((RelativeLayout) getActivity().getWindow().getDecorView().findViewById(R.id.menuHome)).setVisibility(View.VISIBLE);
        ((CustomTextView) getActivity().getWindow().getDecorView().findViewById(R.id.title)).setText("");
        ButterKnife.inject(this, view);

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                ChatMain fragment = new ChatMain();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment, fragment, "ChatMain");
                ft.addToBackStack("ChatMain");
                ft.commit();
            }
        });
        try {
            name.setText(getActivity().getString(R.string.hi) + " " + jsonObject.getString("first_name_en") + "\n" + getActivity().getString(R.string.whatNext));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.onlineConsult)
    public void onOnlineConsultClicked() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Bundle bundle = new Bundle();
        try {
            bundle.putString(WebService.HomeVisit.id_user, jsonObject.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        com.example.snosey.balto.main.online_consultation.Main fragment = new com.example.snosey.balto.main.online_consultation.Main();
        FragmentTransaction ft = fm.beginTransaction();
        fragment.setArguments(bundle);
        ft.replace(R.id.fragment, fragment, "Main");
        ft.addToBackStack("Main");
        ft.commit();
    }

    @OnClick(R.id.homeVisiting)
    public void onHomeVisitingClicked() {
        if (true) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.soon), Toast.LENGTH_LONG).show();
            return;
        }
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Bundle bundle = new Bundle();
        try {
            bundle.putString(WebService.HomeVisit.id_user, jsonObject.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Main fragment = new Main();
        FragmentTransaction ft = fm.beginTransaction();
        fragment.setArguments(bundle);
        ft.replace(R.id.fragment, fragment, "Main");
        ft.addToBackStack("Main");
        ft.commit();
    }

    @OnClick(R.id.shareApp)
    public void onShareClicked() {
        UrlData urlData = new UrlData();
        try {
            urlData.add(WebService.PromoCode.added_by, jsonObject.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (output.contains("true")) {
                    String code = "";
                    try {
                        code = new JSONObject(output).getString("code");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            getString(R.string.shareOfferPatient) + code + "\nhttps://play.google.com/store/apps/details?id=" +
                                    BuildConfig.APPLICATION_ID);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);

                }
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.PromoCode.newPromoCodeApi, urlData.get());

    }
}
