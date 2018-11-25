package com.example.snosey.balto.payment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.snosey.balto.MainActivity;
import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.CustomTextView;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.paymob.acceptsdk.IntentConstants;
import com.paymob.acceptsdk.PayResponseKeys;
import com.paymob.acceptsdk.SaveCardResponseKeys;
import com.paymob.acceptsdk.ToastMaker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Snosey on 3/22/2018.
 */

public class PaymentSlider extends android.support.v4.app.Fragment {

    private static final int ACCEPT_PAYMENT_REQUEST = 10;
    final int GET_NEW_CARD = 2;
    final int EDIT_CARD = 5;

    @InjectView(R.id.radioGroup)
    RadioGroup radioGroup;
    @InjectView(R.id.aman)
    RadioButton cash;
    @InjectView(R.id.credit)
    RadioButton credit;
    @InjectView(R.id.wallet)
    RadioButton wallet;
    @InjectView(R.id.addEditCredit)
    com.example.snosey.balto.Support.CustomTextView addEditCredit;

    @InjectView(R.id.saved_card)
    com.example.snosey.balto.Support.CustomTextView saved_card;

    @InjectView(R.id.next)
    ImageButton next;
    @InjectView(R.id.amount)
    EditText amount;

    @InjectView(R.id.phone)
    EditText phone;

    @InjectView(R.id.phoneCashWays)
    CustomTextView phoneCashWays;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private boolean joinAgain = true;
    private RequestQueue MyRequestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payment_way, container, false);
        ButterKnife.inject(this, view);

        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.back)).setVisibility(View.VISIBLE);
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.menu)).setVisibility(View.GONE);

        wallet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    phone.setVisibility(View.VISIBLE);
                    phoneCashWays.setVisibility(View.VISIBLE);
                } else {
                    phone.setVisibility(View.GONE);
                    phoneCashWays.setVisibility(View.GONE);
                }
            }
        });

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
                    new MakePayMobApi(getActivity(), "100", PaymentSlider.this, "", WebService.Payment.payLive1, WebService.Booking.credit);
                }

            }
        });

    /*    cash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
       */
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View view2 = getActivity().getCurrentFocus();
                if (view2 != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
                }

                if (amount.getText().length() == 0) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.wrongAmount), Toast.LENGTH_SHORT).show();
                    return;
                } else if (amount.getText().toString().startsWith("0")) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.wrongAmount), Toast.LENGTH_SHORT).show();
                    return;
                }

                final ProgressBar dialog = getActivity().findViewById(R.id.progress);
                MyRequestQueue = Volley.newRequestQueue(getActivity());
                if (joinAgain) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            joinAgain = true;
                        }
                    }, 5000);
                    String api = "";
                    if (radioGroup.getCheckedRadioButtonId() == R.id.aman)
                        api = WebService.Payment.amanPaymentApi;
                    else if (radioGroup.getCheckedRadioButtonId() == R.id.wallet) {
                        if (phone.getText().toString().startsWith("01") && phone.getText().toString().length() == 11)
                            api = WebService.Payment.walletPaymentApi;
                        else {
                            Toast.makeText(getActivity(), getActivity().getString(R.string.wrongPhone), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        try {
                            if (!MainActivity.jsonObject.getString(WebService.Login.payment_token).equals("") && !MainActivity.jsonObject.getString(WebService.Login.payment_token).equals("null")) {
                                api = WebService.Payment.onlinePaymentApi;
                            } else {
                                Toast.makeText(getActivity(), getActivity().getString(R.string.addOrChangeCredit), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    dialog.setVisibility(View.VISIBLE);
                    StringRequest MyStringRequest = new StringRequest(Request.Method.POST, api, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("Response", response);
                            //This code is executed if the server responds, whether or not the response contains data.
                            //The String 'response' contains the server's response.
                            try {
                                final JSONObject jsonObject = new JSONObject(response);
                                dialog.setVisibility(View.GONE);
                                if (radioGroup.getCheckedRadioButtonId() == R.id.aman) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            final Dialog amanDialog = new Dialog(getActivity(), android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
                                            amanDialog.setContentView(R.layout.aman);
                                            CustomTextView amanText = (CustomTextView) amanDialog.findViewById(R.id.amanText);
                                            AppCompatImageButton amanNext = (AppCompatImageButton) amanDialog.findViewById(R.id.next);
                                            try {
                                                amanText.setText(getActivity().getString(R.string.amanDetails) + " " + jsonObject.getString("data") + getActivity().getString(R.string.amanDetails2));
                                                amanNext.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        amanDialog.dismiss();
                                                        getActivity().onBackPressed();
                                                    }
                                                });
                                                amanDialog.show();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else if (radioGroup.getCheckedRadioButtonId() == R.id.wallet) {
                                    Intent browserIntent = new Intent(getActivity(), com.example.snosey.balto.Support.WebView.class);
                                    browserIntent.putExtra("url", jsonObject.getString("data"));
                                    startActivity(browserIntent);
                                } else {
                                    if (jsonObject.getString(WebService.Payment.status).equals("1")) {
                                        try {
                                            dialog.setVisibility(View.VISIBLE);
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            dialog.setVisibility(View.GONE);
                                                            getActivity().onBackPressed();
                                                        }
                                                    });
                                                }
                                            }, 3000);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else
                                        Toast.makeText(getActivity(), "Ops,Failed", Toast.LENGTH_SHORT).show();
                                }
                            } catch (
                                    JSONException e) {
                                Toast.makeText(getActivity(), getActivity().getString(R.string.error_null_cursor), Toast.LENGTH_SHORT).show();
                                dialog.setVisibility(View.GONE);
                                e.printStackTrace();
                            }
                        }
                    },
                            new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    Toast.makeText(getActivity(), getActivity().getString(R.string.error_null_cursor), Toast.LENGTH_SHORT).show();
                                    dialog.setVisibility(View.GONE);
                                    //This code is executed if there is an error.
                                }
                            })

                    {
                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            HashMap<String, String> MyData = new HashMap<String, String>();
                            try {
                                MyData.put(WebService.Payment.user_id, MainActivity.jsonObject.getString(WebService.Payment.id));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            MyData.put(WebService.Payment.amount, amount.getText().toString());
                            if (radioGroup.getCheckedRadioButtonId() == R.id.credit)
                                MyData.put(WebService.Payment.state, WebService.Payment.online);

                            if (radioGroup.getCheckedRadioButtonId() == R.id.wallet)
                                MyData.put(WebService.Payment.phone, phone.getText().toString());

                            Log.e("MyData", MyData.toString());
                            return new JSONObject(MyData).toString().getBytes();
                        }

                        @Override
                        public String getBodyContentType() {
                            return "application/json";
                        }

                    };
                    MyStringRequest.setRetryPolicy(new DefaultRetryPolicy(
                            0,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    MyRequestQueue.add(MyStringRequest);
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

