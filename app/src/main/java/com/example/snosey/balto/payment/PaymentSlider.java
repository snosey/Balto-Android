package com.example.snosey.balto.payment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.example.snosey.balto.MainActivity;
import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.paymob.acceptsdk.IntentConstants;
import com.paymob.acceptsdk.PayResponseKeys;
import com.paymob.acceptsdk.SaveCardResponseKeys;
import com.paymob.acceptsdk.ToastMaker;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Snosey on 3/22/2018.
 */

public class PaymentSlider extends android.support.v4.app.Fragment {

    private static final int ACCEPT_PAYMENT_REQUEST = 10;
    final int GET_NEW_CARD = 2;
    final int EDIT_CARD = 5;

    @InjectView(R.id.cash)
    RadioButton cash;
    @InjectView(R.id.credit)
    RadioButton credit;
    @InjectView(R.id.addEditCredit)
    com.example.snosey.balto.Support.CustomTextView addEditCredit;

    @InjectView(R.id.saved_card)
    com.example.snosey.balto.Support.CustomTextView saved_card;

    @InjectView(R.id.next)
    ImageButton next;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private boolean joinAgain = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payment_way, container, false);
        ButterKnife.inject(this, view);

        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.back)).setVisibility(View.VISIBLE);
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.menu)).setVisibility(View.GONE);


        try {
            if (!MainActivity.jsonObject.getString(WebService.Login.payment_token).equals("") && !MainActivity.jsonObject.getString(WebService.Login.payment_token).equals("null")) {
                saved_card.setText(MainActivity.jsonObject.getString(WebService.Login.card_number));
                if (MainActivity.jsonObject.getString(WebService.Login.card_type).equals("MasterCard"))
                    saved_card.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.master_card, 0, 0, 0);
                else
                    saved_card.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.visa, 0, 0, 0);

            } else
                saved_card.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sharedPreferences = getActivity().getSharedPreferences("payment", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (sharedPreferences.contains(WebService.Credit.paymentWay)) {
            if (sharedPreferences.getString(WebService.Credit.paymentWay, "").equals(WebService.Credit.cash))
                cash.setChecked(true);
            else credit.setChecked(true);
        }
        Typeface font = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/arial.ttf");
        addEditCredit.setTypeface(font, Typeface.BOLD);
        addEditCredit.setPaintFlags(addEditCredit.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        addEditCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (joinAgain) {
                    joinAgain = false;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            joinAgain = true;
                        }
                    }, 5000);
                    new MakePayMobApi(getActivity(), "100", PaymentSlider.this, "", WebService.Payment.payLive1);
                }

            }
        });

        cash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    editor.putString(WebService.Credit.paymentWay, WebService.Credit.cash);
                    editor.commit();
                }
            }
        });
        credit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    try {
                        if (MainActivity.jsonObject.getString("payment_token").equals("null") || MainActivity.jsonObject.getString("payment_token").equals("")) {
                            new MakePayMobApi(getActivity(), "100", PaymentSlider.this, "", WebService.Payment.payLive1);
                        } else {
                            editor.putString(WebService.Credit.paymentWay, WebService.Credit.credit);
                            editor.commit();
                            //Intent intent = new Intent(getActivity(), CardEditActivity.class);
                            //startActivityForResult(intent, GET_NEW_CARD);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (joinAgain) {
                    joinAgain = false;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            joinAgain = true;
                        }
                    }, 5000);
                    new MakePayMobApi(getActivity(), "100", PaymentSlider.this, "", WebService.Payment.payLive1);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (data == null)
            return;

        if (requestCode == ACCEPT_PAYMENT_REQUEST) {
            Bundle extras = null;
            try {
                extras = data.getExtras();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            if (resultCode == IntentConstants.USER_CANCELED) {
                // User canceled and did no payment request was fired
                ToastMaker.displayShortToast(getActivity(), "User canceled!!");
            } else if (resultCode == IntentConstants.MISSING_ARGUMENT) {
                // You forgot to pass an important key-value pair in the intent's extras
                ToastMaker.displayShortToast(getActivity(), "Missing Argument == " + extras.getString(IntentConstants.MISSING_ARGUMENT_VALUE));
            } else if (resultCode == IntentConstants.TRANSACTION_ERROR) {
                // An error occurred while handling an API's response
                ToastMaker.displayShortToast(getActivity(), "Reason == " + extras.getString(IntentConstants.TRANSACTION_ERROR_REASON));
            } else if (resultCode == IntentConstants.TRANSACTION_REJECTED) {
                // User attempted to pay but their transaction was rejected

                // Use the static keys declared in PayResponseKeys to extract the fields you want
                ToastMaker.displayShortToast(getActivity(), extras.getString(PayResponseKeys.DATA_MESSAGE));
            } else if (resultCode == IntentConstants.TRANSACTION_REJECTED_PARSING_ISSUE) {
                // User attempted to pay but their transaction was rejected. An error occured while reading the returned JSON
                ToastMaker.displayShortToast(getActivity(), extras.getString(IntentConstants.RAW_PAY_RESPONSE));
            } else if (resultCode == IntentConstants.TRANSACTION_SUCCESSFUL) {
                // User finished their payment successfully

                // Use the static keys declared in PayResponseKeys to extract the fields you want
                ToastMaker.displayShortToast(getActivity(), extras.getString(PayResponseKeys.DATA_MESSAGE));
            } else if (resultCode == IntentConstants.TRANSACTION_SUCCESSFUL_PARSING_ISSUE) {
                // User finished their payment successfully. An error occured while reading the returned JSON.
                ToastMaker.displayShortToast(getActivity(), "TRANSACTION_SUCCESSFUL - Parsing Issue");

                // ToastMaker.displayShortToast(getActivity(), extras.getString(IntentConstants.RAW_PAY_RESPONSE));
            } else if (resultCode == IntentConstants.TRANSACTION_SUCCESSFUL_CARD_SAVED) {
                saveCard(extras.getString(SaveCardResponseKeys.TOKEN), extras.getString(SaveCardResponseKeys.CARD_SUBTYPE), extras.getString(SaveCardResponseKeys.MASKED_PAN));
                {
                    saved_card.setText(extras.getString(SaveCardResponseKeys.MASKED_PAN));
                    if (extras.getString(SaveCardResponseKeys.MASKED_PAN).equals("MasterCard"))
                        saved_card.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.master_card, 0, 0, 0);
                    else
                        saved_card.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.visa, 0, 0, 0);

                }
                getActivity().onBackPressed();
            } else if (resultCode == IntentConstants.USER_CANCELED_3D_SECURE_VERIFICATION) {
                ToastMaker.displayShortToast(getActivity(), "User canceled 3-d scure verification!!");

                // Note that a payment process was attempted. You can extract the original returned values
                // Use the static keys declared in PayResponseKeys to extract the fields you want
                ToastMaker.displayShortToast(getActivity(), extras.getString(PayResponseKeys.PENDING));
            } else if (resultCode == IntentConstants.USER_CANCELED_3D_SECURE_VERIFICATION_PARSING_ISSUE) {
                ToastMaker.displayShortToast(getActivity(), "User canceled 3-d scure verification - Parsing Issue!!");

                // Note that a payment process was attempted.
                // User finished their payment successfully. An error occured while reading the returned JSON.
                ToastMaker.displayShortToast(getActivity(), extras.getString(IntentConstants.RAW_PAY_RESPONSE));
            }
        }
    }

    private void saveCard(String token, final String type, final String number) {
        UrlData urlData = new UrlData();
        urlData.add("payment_token", token);
        urlData.add(WebService.Login.card_number, number);
        urlData.add(WebService.Login.card_type, type);
        try {
            urlData.add("id", MainActivity.jsonObject.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                JSONObject jsonObject = new JSONObject(output);
                MainActivity.jsonObject.put("payment_token", jsonObject.getJSONObject("user").getString("payment_token"));
                MainActivity.jsonObject.put(WebService.Login.card_type, type);
                MainActivity.jsonObject.put(WebService.Login.card_number, number);
                ToastMaker.displayShortToast(getActivity(), "Saved");
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Setting.updateUserApi, urlData.get());

    }


}

