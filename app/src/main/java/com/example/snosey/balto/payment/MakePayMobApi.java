package com.example.snosey.balto.payment;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
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
    private final RequestQueue MyRequestQueue;
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
        MyRequestQueue = Volley.newRequestQueue(activity);
        firstStep();
    }

    public void firstStep() {
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, WebService.Credit.askForAuthApi, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response", response);
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
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
                MyData.put("username", "Elbalto"); //Add the data you'd like to send to the server.
                MyData.put("password", "Ec0n0mics@88");
                MyData.put("expiration", "36000");
                Log.e("MyData", MyData.toString());
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

        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, WebService.Credit.orderApi + "?token=" + token, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response", response);
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
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
                Log.e("MyData", MyData.toString());
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
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, WebService.Credit.paymentKeyRequestApi + "?token=" + token, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response", response);
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    //add id of payment in payment database
                    paymentKey = jsonObject.getString("token");
                    if (HasToken) {
                        payNowAman();
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
            /*    if (HasToken) {
                    MyData.put("token", TOKEN);
                }*/
                MyData.put("integration_id", integrationId);
                MyData.put("currency", "EGP");
                MyData.put("amount_cents", price);
                JSONObject billing = new JSONObject();
                try {
                    billing.put("first_name", MainActivity.jsonObject.getString(WebService.SignUp.first_name_en));
                    billing.put("last_name", MainActivity.jsonObject.getString(WebService.SignUp.last_name_en));
                    billing.put("street", "NA");
                    billing.put("building", "NA");
                    billing.put("apartment", "NA");
                    billing.put("city", "NA");
                    billing.put("state", "NA");
                    billing.put("country", "Egypt");
                    if (!MainActivity.jsonObject.getString(WebService.SignUp.email).equals(""))
                        billing.put("email", MainActivity.jsonObject.getString(WebService.SignUp.email));
                    else
                        billing.put("email", "NA");
                    billing.put("phone_number", MainActivity.jsonObject.getString(WebService.SignUp.phone));
                    billing.put("postal_code", "NA");

                    billing.put("floor", "NA");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MyData.put("billing_data", billing);

                Log.e("MyData", MyData.toString());

                return new JSONObject(MyData).toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

        };
        MyRequestQueue.add(MyStringRequest);
    }

    private void payNowVisa() {

        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, WebService.Credit.payNowApi, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Pay Response", response);
                dialog.setVisibility(View.GONE);
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
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
                    error.printStackTrace();

                    if (error.networkResponse.data != null) {

                        try {
                            body = new String(error.networkResponse.data, "UTF-8");
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
                    billing.put("first_name", MainActivity.jsonObject.getString(WebService.SignUp.first_name_en));
                    billing.put("last_name", MainActivity.jsonObject.getString(WebService.SignUp.last_name_en));
                    billing.put("street", "NA");
                    billing.put("building", "NA");
                    billing.put("floor", "NA");
                    billing.put("apartment", "NA");
                    billing.put("city", "NA");
                    billing.put("state", "NA");
                    billing.put("country", "Egypt");
                    if (!MainActivity.jsonObject.getString(WebService.SignUp.email).equals(""))
                        billing.put("email", MainActivity.jsonObject.getString(WebService.SignUp.email));
                    else
                        billing.put("email", "NA");
                    billing.put("phone_number", MainActivity.jsonObject.getString(WebService.SignUp.phone));
                    billing.put("postal_code", "NA");


                    MyData.put("source", source);
                    MyData.put("billing", billing);
                    MyData.put("payment_token", paymentKey);

                    Log.e("MyData", MyData.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return MyData.toString().getBytes();
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


    private void payNowWallet() {

        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, WebService.Credit.payNowApi, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Pay Response", response);
                dialog.setVisibility(View.GONE);
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
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
                    error.printStackTrace();

                    if (error.networkResponse.data != null) {

                        try {
                            body = new String(error.networkResponse.data, "UTF-8");
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
                    source.put("identifier", "01274155230");
                    source.put("subtype", "WALLET");

                 /*   JSONObject billing = new JSONObject();
                    billing.put("first_name", MainActivity.jsonObject.getString(WebService.SignUp.first_name_en));
                    billing.put("last_name", MainActivity.jsonObject.getString(WebService.SignUp.last_name_en));
                    billing.put("street", "NA");
                    billing.put("building", "NA");
                    billing.put("floor", "NA");
                    billing.put("apartment", "NA");
                    billing.put("city", "NA");
                    billing.put("state", "NA");
                    billing.put("country", "Egypt");
                    if (!MainActivity.jsonObject.getString(WebService.SignUp.email).equals(""))
                        billing.put("email", MainActivity.jsonObject.getString(WebService.SignUp.email));
                    else
                        billing.put("email", "NA");
                    billing.put("phone_number", MainActivity.jsonObject.getString(WebService.SignUp.phone));
                    billing.put("postal_code", "NA");

*/
                    MyData.put("source", source);
                    // MyData.put("billing", billing);
                    MyData.put("payment_token", paymentKey);

                    Log.e("MyData", MyData.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return MyData.toString().getBytes();
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


    private void payNowAman() {

        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, WebService.Credit.payNowApi, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Pay Response", response);
                dialog.setVisibility(View.GONE);
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
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
                    error.printStackTrace();

                    if (error.networkResponse.data != null) {

                        try {
                            body = new String(error.networkResponse.data, "UTF-8");
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
                    source.put("identifier", "AGGREGATOR");
                    source.put("subtype", "AMAN");

                 /*   JSONObject billing = new JSONObject();
                    billing.put("first_name", MainActivity.jsonObject.getString(WebService.SignUp.first_name_en));
                    billing.put("last_name", MainActivity.jsonObject.getString(WebService.SignUp.last_name_en));
                    billing.put("street", "NA");
                    billing.put("building", "NA");
                    billing.put("floor", "NA");
                    billing.put("apartment", "NA");
                    billing.put("city", "NA");
                    billing.put("state", "NA");
                    billing.put("country", "Egypt");
                    if (!MainActivity.jsonObject.getString(WebService.SignUp.email).equals(""))
                        billing.put("email", MainActivity.jsonObject.getString(WebService.SignUp.email));
                    else
                        billing.put("email", "NA");
                    billing.put("phone_number", MainActivity.jsonObject.getString(WebService.SignUp.phone));
                    billing.put("postal_code", "NA");

*/
                    MyData.put("source", source);
                    // MyData.put("billing", billing);
                    MyData.put("payment_token", paymentKey);

                    Log.e("MyData", MyData.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return MyData.toString().getBytes();
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


    protected void startPayActivityNoToken() {
        dialog.setVisibility(View.GONE);
        Intent pay_intent = new Intent(activity, PayActivity.class);
        // Pass the correct values for the billing data keys
        try {
            putNormalExtras(pay_intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            fragment.startActivityForResult(pay_intent, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void putNormalExtras(Intent pay_intent) throws JSONException {

        pay_intent.putExtra(PayActivityIntentKeys.PAYMENT_KEY, paymentKey);
        pay_intent.putExtra(PayActivityIntentKeys.FIRST_NAME, MainActivity.jsonObject.getString(WebService.SignUp.first_name_en));
        pay_intent.putExtra(PayActivityIntentKeys.LAST_NAME, MainActivity.jsonObject.getString(WebService.SignUp.last_name_en));
        pay_intent.putExtra(PayActivityIntentKeys.BUILDING, "NA");
        pay_intent.putExtra(PayActivityIntentKeys.FLOOR, "NA");
        pay_intent.putExtra(PayActivityIntentKeys.APARTMENT, "NA");
        pay_intent.putExtra(PayActivityIntentKeys.CITY, "NA");
        pay_intent.putExtra(PayActivityIntentKeys.STATE, "NA");
        pay_intent.putExtra(PayActivityIntentKeys.COUNTRY, "Egypt");
        pay_intent.putExtra(PayActivityIntentKeys.EMAIL, MainActivity.jsonObject.getString(WebService.SignUp.email));

        if (!MainActivity.jsonObject.getString(WebService.SignUp.email).equals(""))
            pay_intent.putExtra(PayActivityIntentKeys.EMAIL, MainActivity.jsonObject.getString(WebService.SignUp.email));
        else
            pay_intent.putExtra(PayActivityIntentKeys.EMAIL, "NA");

        pay_intent.putExtra(PayActivityIntentKeys.PHONE_NUMBER, MainActivity.jsonObject.getString(WebService.SignUp.phone));
        pay_intent.putExtra(PayActivityIntentKeys.POSTAL_CODE, "NA");
        pay_intent.putExtra(PayActivityIntentKeys.THREE_D_SECURE_ACTIVITY_TITLE, "Verification");
        pay_intent.putExtra(PayActivityIntentKeys.SAVE_CARD_DEFAULT, true);
        pay_intent.putExtra(PayActivityIntentKeys.SHOW_SAVE_CARD, false);
        pay_intent.putExtra(PayActivityIntentKeys.SHOW_ALERTS, false);
        pay_intent.putExtra(PayActivityIntentKeys.THEME_COLOR, Color.parseColor("#2f9cab"));
    }

}
