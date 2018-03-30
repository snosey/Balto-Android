package com.example.snosey.balto.main;

import android.app.Dialog;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.example.snosey.balto.MainActivity;
import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;

import org.json.JSONException;

/**
 * Created by Snosey on 3/20/2018.
 */

public class RateDialog extends Dialog {

    public RateDialog(@NonNull final FragmentActivity context, final String bookingId, String id_user) {
        super(context);
        this.setContentView(R.layout.rate_dialog);
        Button thankYou = (Button) this.findViewById(R.id.thankYou);
        final EditText review = (EditText) this.findViewById(R.id.review);
        final RatingBar rate = (RatingBar) this.findViewById(R.id.rate);
        thankYou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UrlData urlData = new UrlData();
                urlData.add(WebService.Booking.id_booking, bookingId);
                try {
                    urlData.add(WebService.Booking.id_user, MainActivity.jsonObject.getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                urlData.add(WebService.Booking.review, review.getText().toString());
                urlData.add(WebService.Booking.rate, ((int) rate.getRating()) + "");
                new GetData(new GetData.AsyncResponse() {
                    @Override
                    public void processFinish(String output) throws JSONException {
                        dismiss();
                    }
                }, context, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Booking.rateBookingApi, urlData.get());
            }
        });

    }
}
