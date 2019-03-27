package com.example.snosey.balto.Support.webservice;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by Snosey on 4/3/2018.
 */

public class WebServiceVolley extends Request<String> {
    public static final String Data = "Data";
    static final String Status = "status";
    public static final String Id = "Id";
    static FragmentActivity activity;
    String url;
    public final static String BASE_URL = "https://gatherwebservice.d-innova.com/"; //new
    //final static String BASE_URL = " https://2by2.d-innova.com/"; //old
    ProgressDialog progress = null;
    JSONObject params;
    private Response.Listener<String> mListener;
    boolean showLoading;
    String messageAlert;

    public WebServiceVolley(final FragmentActivity activity, int method, final String url, final boolean showLoading,
                            String messageAlert, JSONObject params, Response.Listener<String> listener) {
        super(method, url, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.

            @Override
            public void onErrorResponse(VolleyError error) {

                //This code is executed if there is an error.
                String body;

                //get status code here
                //get response body and parse with appropriate encoding
                try {
                    Log.e("Error Url", url);
                    if (error.networkResponse.data != null) {
                        try {
                            body = new String(error.networkResponse.data, "UTF-8");
                            Log.e("Error:", body);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        this.activity = activity;
        mListener = listener;
        this.messageAlert = messageAlert;
        this.showLoading = showLoading;
        this.params = params;
        this.url = url;
        Log.e("API/URL", this.url);
        this.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        progress = new ProgressDialog(activity);
        progress.setMessage("Loading, please wait...");
        progress.setCancelable(true); // disable dismiss by tapping outside of the dialog

        if (showLoading) {
            Log.e("ProgressShow", "true");
            progress.show();
        }
    }

    public void sendRequest() {
        Volley.newRequestQueue(activity).add(this);
    }

    private static boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "Server error, contact with support to solve the problem", Toast.LENGTH_LONG).show();
            }
        });
        progress.dismiss();
        return volleyError;
    }


    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        // since we don't know which of the two underlying network vehicles
        // will Volley use, we have to handle and store session cookies manually

        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }


        String modifiedResponse = Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response)).result;
        Log.e("modifiedResponse", modifiedResponse);
        try {
            progress.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
        progress.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (!jsonObject.getBoolean(Status)) {
                if (showLoading && messageAlert.length() != 0)
                    Toast.makeText(activity, messageAlert, Toast.LENGTH_LONG).show();
                else {
                    mListener.onResponse(response);
                }
            } else {
                mListener.onResponse(response);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBodyContentType() {
        return "application/json";
    }

    @Override
    public byte[] getBody() {
        Log.e("params:", params.toString());
        int length = params.toString().getBytes().length;
        ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
        bos.write(params.toString().getBytes(), 0, length);
        return bos.toByteArray();
    }

    public static String convertFile(File file) {
        InputStream inputStream = null;//You can get an inputStream using any IO API
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] bytes;
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        bytes = output.toByteArray();
        String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
        return encodedString;

    }

    public static String getMimeType(File file) {
        String mimeType = null;
        Uri uri = Uri.fromFile(file);
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = activity.getApplicationContext().getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            return  fileExtension;
            /*
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());*/
        }
        return mimeType;
    }

    public static class MapStyle {
        public static String mapStyle = "[\n" + "    {\n" +
                "        \"featureType\": \"all\",\n" +
                "        \"elementType\": \"labels.text.fill\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"color\": \"#7c93a3\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"lightness\": \"-10\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"administrative.country\",\n" +
                "        \"elementType\": \"geometry\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"visibility\": \"on\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"administrative.country\",\n" +
                "        \"elementType\": \"geometry.stroke\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"color\": \"#a0a4a5\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"administrative.province\",\n" +
                "        \"elementType\": \"geometry.stroke\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"color\": \"#62838e\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"landscape\",\n" +
                "        \"elementType\": \"geometry.fill\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"color\": \"#dde3e3\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"landscape.man_made\",\n" +
                "        \"elementType\": \"geometry.stroke\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"color\": \"#3f4a51\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"weight\": \"0.30\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"poi\",\n" +
                "        \"elementType\": \"all\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"visibility\": \"simplified\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"poi.attraction\",\n" +
                "        \"elementType\": \"all\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"visibility\": \"on\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"poi.business\",\n" +
                "        \"elementType\": \"all\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"visibility\": \"off\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"poi.government\",\n" +
                "        \"elementType\": \"all\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"visibility\": \"off\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"poi.park\",\n" +
                "        \"elementType\": \"all\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"visibility\": \"on\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"poi.place_of_worship\",\n" +
                "        \"elementType\": \"all\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"visibility\": \"off\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"poi.school\",\n" +
                "        \"elementType\": \"all\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"visibility\": \"off\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"poi.sports_complex\",\n" +
                "        \"elementType\": \"all\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"visibility\": \"off\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"road\",\n" +
                "        \"elementType\": \"all\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"saturation\": \"-100\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"visibility\": \"on\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"road\",\n" +
                "        \"elementType\": \"geometry.stroke\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"visibility\": \"on\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"road.highway\",\n" +
                "        \"elementType\": \"geometry.fill\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"color\": \"#bbcacf\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"road.highway\",\n" +
                "        \"elementType\": \"geometry.stroke\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"lightness\": \"0\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"color\": \"#bbcacf\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"weight\": \"0.50\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"road.highway\",\n" +
                "        \"elementType\": \"labels\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"visibility\": \"on\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"road.highway\",\n" +
                "        \"elementType\": \"labels.text\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"visibility\": \"on\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"road.highway.controlled_access\",\n" +
                "        \"elementType\": \"geometry.fill\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"color\": \"#ffffff\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"road.highway.controlled_access\",\n" +
                "        \"elementType\": \"geometry.stroke\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"color\": \"#a9b4b8\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"road.arterial\",\n" +
                "        \"elementType\": \"labels.icon\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"invert_lightness\": true\n" +
                "            },\n" +
                "            {\n" +
                "                \"saturation\": \"-7\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"lightness\": \"3\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"gamma\": \"1.80\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"weight\": \"0.01\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"transit\",\n" +
                "        \"elementType\": \"all\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"visibility\": \"off\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"water\",\n" +
                "        \"elementType\": \"geometry.fill\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"color\": \"#a3c7df\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "]";

    }
}
