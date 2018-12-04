package com.example.snosey.balto.main;

import android.app.Dialog;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;

import org.json.JSONException;

/**
 * Created by Snosey on 3/20/2018.
 */

public class UpdateRateDialog extends Dialog {

    public UpdateRateDialog(@NonNull final FragmentActivity context, final String rateId, String rateText, String rateCount) {
        super(context);
        this.setContentView(R.layout.edit_rate_dialog);
        Button thankYou = (Button) this.findViewById(R.id.thankYou);
        Button delete = (Button) this.findViewById(R.id.delete);
        final EditText review = (EditText) this.findViewById(R.id.review);
        review.setText(rateText);
        final RatingBar rate = (RatingBar) this.findViewById(R.id.rate);
        rate.setRating(Integer.parseInt(rateCount));
        thankYou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UrlData urlData = new UrlData();
                urlData.add(WebService.Booking.id, rateId);
                urlData.add(WebService.Booking.review, review.getText().toString());
                urlData.add(WebService.Booking.rate, ((int) rate.getRating()) + "");
                new GetData(new GetData.AsyncResponse() {
                    @Override
                    public void processFinish(String output) throws JSONException {
                        dismiss();
                    }
                }, context, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Booking.editRateBookingApi, urlData.get());
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UrlData urlData = new UrlData();
                urlData.add(WebService.Booking.id, rateId);
                new GetData(new GetData.AsyncResponse() {
                    @Override
                    public void processFinish(String output) throws JSONException {
                        dismiss();
                    }
                }, context, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Booking.deleteRateApi, urlData.get());
            }
        });
    }
}
