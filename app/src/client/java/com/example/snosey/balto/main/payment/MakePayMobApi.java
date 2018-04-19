package com.example.snosey.balto.main.payment;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.WebService;
import com.paymob.acceptsdk.PayActivity;
import com.paymob.acceptsdk.PayActivityIntentKeys;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Created by Snosey on 3/22/2018.
 */

public class MakePayMobApi {
    FragmentActivity activity;
    private String price;
    String TOKEN;
    String orderId;
    ProgressBar dialog;
    Fragment fragment;
    private String paymentKey;
    boolean HasToken = true;
    String integrationId;

    public MakePayMobApi(FragmentActivity activity, String price, Fragment agenda, String token, String integrationId) {
        this.activity = activity;
        this.integrationId = integrationId;
        this.price = price;
        this.fragment = agenda;
        this.TOKEN = token;
        if (TOKEN.equals("null") || TOKEN.equals(""))
            HasToken = false;
        dialog = activity.findViewById(R.id.progress);
        try {
            if (dialog.getVisibility() == View.GONE) {
                dialog.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {

        }
        firstStep();
    }

    public void firstStep() {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(activity);
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, WebService.Credit.askForAuthApi, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                Log.e("response:", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    secondStep(jsonObject.getString("token"), jsonObject.getJSONObject("profile").getString("id"));
                } catch (JSONException e) {

                    Toast.makeText(activity, activity.getString(R.string.error_null_cursor), Toast.LENGTH_SHORT).show();
                    dialog.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(activity, activity.getString(R.string.error_null_cursor), Toast.LENGTH_SHORT).show();
                dialog.setVisibility(View.GONE);
                //This code is executed if there is an error.
            }
        })

        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                HashMap<String, String> MyData = new HashMap<String, String>();
                return new JSONObject(MyData).toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

        };
        MyRequestQueue.add(MyStringRequest);
    }

    private void secondStep(final String token, final String merchant_id) {

        RequestQueue MyRequestQueue = Volley.newRequestQueue(activity);
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, WebService.Credit.orderApi + "?token=" + token, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                Log.e("response:", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    //add id of payment in payment database
                    thirdStep(jsonObject.getString("id"), token);
                } catch (JSONException e) {

                    Toast.makeText(activity, activity.getString(R.string.error_null_cursor), Toast.LENGTH_SHORT).show();
                    dialog.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(activity, activity.getString(R.string.error_null_cursor), Toast.LENGTH_SHORT).show();
                dialog.setVisibility(View.GONE);
                //This code is executed if there is an error.
            }
        })

        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                HashMap<String, String> MyData = new HashMap<String, String>();
                MyData.put("merchant_id", merchant_id);
                MyData.put("amount_cents", price);
                return new JSONObject(MyData).toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

        };
        MyRequestQueue.add(MyStringRequest);
    }

    private void thirdStep(final String id, final String token) {

        if (!HasToken) dialog.setVisibility(View.GONE);
        RequestQueue MyRequestQueue = Volley.newRequestQueue(activity);
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, WebService.Credit.paymentKeyRequestApi + "?token=" + token, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                Log.e("response:", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    //add id of payment in payment database
                    paymentKey = jsonObject.getString("token");
                    if (HasToken) {
                        payNow();
                    } else {
                        startPayActivityNoToken();
                    }
                } catch (JSONException e) {
                    Toast.makeText(activity, activity.getString(R.string.error_null_cursor), Toast.LENGTH_SHORT).show();
                    dialog.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.

                Toast.makeText(activity, activity.getString(R.string.error_null_cursor), Toast.LENGTH_SHORT).show();
                dialog.setVisibility(View.GONE);
            }
        })

        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                HashMap<String, Object> MyData = new HashMap<String, Object>();
                MyData.put("order_id", id); //Add the data you'd like to send to the server.
                MyData.put("expiration", "36000");
                if (HasToken) {
                    MyData.put("token", TOKEN);
                }
                MyData.put("integration_id", integrationId);
                MyData.put("currency", "EGP");
                MyData.put("amount_cents", price);
                return new JSONObject(MyData).toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

        };
        MyRequestQueue.add(MyStringRequest);
    }

    private void payNow() {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(activity);
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, WebService.Credit.payNowApi, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.setVisibility(View.GONE);
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                Log.e("response:", response);
                Intent intent = new Intent();
                intent.putExtra("response", response);
                fragment.onActivityResult(6666, 6666, intent);
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                String body;
                dialog.setVisibility(View.GONE);
                //get status code here
                //get response body and parse with appropriate encoding

                try {
                    if (error.networkResponse.data != null) {

                        try {
                            body = new String(error.networkResponse.data, "UTF-8");
                            Log.e("Error:", body);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            Toast.makeText(activity, activity.getString(R.string.error_null_cursor), Toast.LENGTH_SHORT).show();
                            dialog.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(activity, activity.getString(R.string.error_null_cursor), Toast.LENGTH_SHORT).show();
                        dialog.setVisibility(View.GONE);

                    }
                } catch (Exception e) {
                    Toast.makeText(activity, activity.getString(R.string.error_null_cursor), Toast.LENGTH_SHORT).show();
                    dialog.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        }
        )

        {
            @Override
            public byte[] getBody() throws AuthFailureError {

                JSONObject MyData = new JSONObject();
                try {

                    JSONObject source = new JSONObject();
                    source.put("identifier", TOKEN);
                    source.put("subtype", "TOKEN");
                    source.put("cvn", "123");

                    JSONObject billing = new JSONObject();
                    billing.put("first_name", "NA");
                    billing.put("last_name", "NA");
                    billing.put("street", "NA");
                    billing.put("building", "NA");
                    billing.put("floor", "NA");
                    billing.put("apartment", "NA");
                    billing.put("city", "NA");
                    billing.put("state", "NA");
                    billing.put("country", "NA");
                    billing.put("email", "NA");
                    billing.put("phone_number", "NA");
                    billing.put("postal_code", "NA");


                    MyData.put("source", source);
                    MyData.put("billing", billing);
                    MyData.put("payment_token", paymentKey);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("My Data:", MyData.toString());
                return MyData.toString().getBytes();

            }


            @Override
            public String getBodyContentType() {
                return "application/json";
            }

        };
        MyRequestQueue.add(MyStringRequest);
    }

    protected void startPayActivityNoToken() {
        dialog.setVisibility(View.GONE);
        Intent pay_intent = new Intent(activity, PayActivity.class);
        // Pass the correct values for the billing data keys
        putNormalExtras(pay_intent);

        try {
            fragment.startActivityForResult(pay_intent, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startPayActivityToken() {
        Intent pay_intent = new Intent(activity, PayActivity.class);

        putNormalExtras(pay_intent);
        // replace this with your actual card token
        pay_intent.putExtra(PayActivityIntentKeys.TOKEN, TOKEN);
        pay_intent.putExtra(PayActivityIntentKeys.MASKED_PAN_NUMBER, "xxxx-xxxx-xxxx-1234");
        fragment.startActivityForResult(pay_intent, 10);
    }

    private void putNormalExtras(Intent pay_intent) {

        pay_intent.putExtra(PayActivityIntentKeys.PAYMENT_KEY, paymentKey);
        pay_intent.putExtra(PayActivityIntentKeys.FIRST_NAME, "firsy_name");
        pay_intent.putExtra(PayActivityIntentKeys.LAST_NAME, "last_name");
        pay_intent.putExtra(PayActivityIntentKeys.BUILDING, "1");
        pay_intent.putExtra(PayActivityIntentKeys.FLOOR, "1");
        pay_intent.putExtra(PayActivityIntentKeys.APARTMENT, "1");
        pay_intent.putExtra(PayActivityIntentKeys.CITY, "cairo");
        pay_intent.putExtra(PayActivityIntentKeys.STATE, "new_cairo");
        pay_intent.putExtra(PayActivityIntentKeys.COUNTRY, "egypt");
        pay_intent.putExtra(PayActivityIntentKeys.EMAIL, "email@gmail.com");
        pay_intent.putExtra(PayActivityIntentKeys.PHONE_NUMBER, "2345678");
        pay_intent.putExtra(PayActivityIntentKeys.POSTAL_CODE, "3456");
        pay_intent.putExtra(PayActivityIntentKeys.THREE_D_SECURE_ACTIVITY_TITLE, "Verification");
        pay_intent.putExtra(PayActivityIntentKeys.SAVE_CARD_DEFAULT, true);
        pay_intent.putExtra(PayActivityIntentKeys.SHOW_SAVE_CARD, false);
        pay_intent.putExtra(PayActivityIntentKeys.SHOW_ALERTS, true);
        pay_intent.putExtra(PayActivityIntentKeys.THEME_COLOR, Color.parseColor("#2f9cab"));
    }

}
