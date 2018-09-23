package com.example.snosey.balto.main.home_visit;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.snosey.balto.MainActivity;
import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.payment.PaymentSlider;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Snosey on 2/21/2018.
 */

public class CategoryDetails extends Fragment {
    @InjectView(R.id.logo)
    ImageView logo;
    @InjectView(R.id.title)
    com.example.snosey.balto.Support.CustomTextView title;
    @InjectView(R.id.details)
    com.example.snosey.balto.Support.CustomTextView details;
    @InjectView(R.id.aman)
    RadioButton cash;
    @InjectView(R.id.credit)
    RadioButton credit;
    @InjectView(R.id.baseFare)
    com.example.snosey.balto.Support.CustomTextView baseFare;
    @InjectView(R.id.serviceDuration)
    com.example.snosey.balto.Support.CustomTextView serviceDuration;
    @InjectView(R.id.durationSeekBar)
    SeekBar durationSeekBar;
    @InjectView(R.id.durationLayout)
    RelativeLayout durationLayout;
    @InjectView(R.id.male)
    RadioButton male;
    @InjectView(R.id.female)
    RadioButton female;
    @InjectView(R.id.both)
    RadioButton both;
    @InjectView(R.id.promoCode)
    EditText promoCode;
    @InjectView(R.id.radioGroup)
    RadioGroup radioGroup;
    @InjectView(R.id.next)
    ImageButton next;
    @InjectView(R.id.confirm)
    Button confirm;

    String id_payment_way = "1";

    String logoText;
    String totalPrice = "0";
    Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_visit_category_details, container, false);
        ButterKnife.inject(this, view);
        try {
            bundle = new Bundle();
            final JSONObject jsonObject = new JSONObject(getArguments().getString("json"));
            bundle.putString(WebService.HomeVisit.id_sub, jsonObject.getString("id"));
            logoText = WebService.Image.fullPathImage + jsonObject.getString("logo");

            Picasso.with(getContext()).load(logoText).into(logo);
            title.setText(jsonObject.getString("name"));
            details.setText(jsonObject.getString("description"));
            baseFare.setText(jsonObject.getString("base_fare"));
            if (jsonObject.getString("max_hour").equals(""))
                durationLayout.setVisibility(View.GONE);
            else {
                Integer max = Integer.parseInt(jsonObject.getString("max_hour"));
                final Integer min = Integer.parseInt(jsonObject.getString("min_hour"));
                serviceDuration.setText(min + "");
                durationSeekBar.setMax((max - min) / 1);
                durationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                        double value = min + progress;
                        serviceDuration.setText(((int) value) + "");
                        try {
                            totalPrice = ((jsonObject.getInt("price_per_hour") * ((int) value - 1)) + "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if (MainActivity.jsonObject.getString("payment_token").equals("null") || MainActivity.jsonObject.getString("payment_token").equals("")) {
                credit.setChecked(false);
                cash.setChecked(true);
            } else {
                cash.setChecked(false);
                credit.setChecked(true);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        cash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    id_payment_way = "1";
                }
            }
        });
        credit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                    //  if (MainActivity.jsonObject.getString("payment_token").equals("null") || MainActivity.jsonObject.getString("payment_token").equals("")) {
                    if (false) {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        PaymentSlider fragment = new PaymentSlider();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.add(R.id.fragment, fragment, "payment");
                        ft.addToBackStack("payment");
                        ft.commit();
                        return;
                    } else {
                        id_payment_way = "2";
                    }
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

    @OnClick(R.id.next)
    public void onNextClicked() {
        try {
            if (MainActivity.jsonObject.getString("payment_token").equals("null") || MainActivity.jsonObject.getString("payment_token").equals(""))
                id_payment_way = "1";
            else if (credit.isChecked())
                id_payment_way = "2";

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String genderId = "3";
        if (radioGroup.getCheckedRadioButtonId() == male.getId())
            genderId = "1";
        else if (radioGroup.getCheckedRadioButtonId() == female.getId())
            genderId = "2";
        else if (radioGroup.getCheckedRadioButtonId() == both.getId())
            genderId = "3";

        bundle.putString(WebService.Booking.id_payment_way, id_payment_way);
        bundle.putString(WebService.HomeVisit.id_gender, genderId);
        bundle.putString("address", getArguments().getString("address"));
        bundle.putString(WebService.HomeVisit.totalPrice, ((Integer.parseInt(totalPrice) +
                Integer.parseInt(baseFare.getText().toString())
        ) + ""));
        bundle.putString(WebService.HomeVisit.duration, serviceDuration.getText().toString());
        bundle.putDouble("lat", getArguments().getDouble("lat"));
        bundle.putDouble("lng", getArguments().getDouble("lng"));
        bundle.putString("name", title.getText().toString());
        bundle.putString("logo_icon", logoText);
        SendRequest fragment = new SendRequest();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        fragment.setArguments(bundle);
        ft.replace(R.id.fragment, fragment, "SendRequest");
        ft.addToBackStack("SendRequest");
        ft.commit();
    }

    @OnClick(R.id.confirm)
    public void onConfirmClicked() {
        View keyboard = getActivity().getCurrentFocus();
        if (keyboard != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(keyboard.getWindowToken(), 0);
        }
        UrlData urlDataPromo = new UrlData();
        urlDataPromo.add(WebService.PromoCode.code, promoCode.getText().toString());
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (output.contains("false"))
                    Toast.makeText(getActivity(), getActivity().getString(R.string.codeIsInvalid), Toast.LENGTH_SHORT).show();
                else {
                    try {
                        JSONObject jsonObject = new JSONObject(output).getJSONObject("coupon");
                        Toast.makeText(getActivity(), jsonObject.getString("coupon_text"), Toast.LENGTH_LONG).show();
                        confirm.setVisibility(View.GONE);
                        promoCode.setEnabled(false);
                        bundle.putString(WebService.HomeVisit.promoCode, promoCode.getText().toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, WebService.PromoCode.promoCodeCheckApi, urlDataPromo.get());

    }
}
