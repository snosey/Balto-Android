package com.example.snosey.balto.Support.webservice;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.snosey.balto.R;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ahmed on 4/23/2017.
 */

//this class for save data using http
public class GetData extends AsyncTask<String, Void, String> {

    public AsyncResponse delegate = null;
    ProgressBar dialog;
    private boolean dialogShown;
    Activity activity;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try { dialog = activity.findViewById(R.id.progress);
            if (dialog.getVisibility() == View.GONE && dialogShown) {
                dialog.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {

        }

    }

    public interface AsyncResponse {
        void processFinish(String output) throws JSONException;
    }

    public GetData(AsyncResponse delegate, Activity activity, boolean dialogShown) {
        this.dialogShown = dialogShown;
        this.delegate = delegate;
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... params) {


        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {

            URL url = new URL(params[0] + params[1].replaceAll(" ", "%20").replaceAll("\n", "%0A"));
            Log.e("sent Url", url.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();


            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
                Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

            }
            return buffer.toString().replace("<meta http-equiv=\"Content-type\" content=\"text/html; charset=UTF-8\" />", "");


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "null";
    }

    @Override
    protected void onPostExecute(String result) {
        try {

            if (result.equals("null")&&dialogShown)
                Toast.makeText(activity, activity.getString(R.string.FailToConnected), Toast.LENGTH_SHORT).show();
            dialog.setVisibility(View.GONE);
            delegate.processFinish(result);
            Log.e("TAG-Result: ", result); // this is expecting a response code to be sent from your server upon receiving the POST data
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPostExecute(result);
    }
}