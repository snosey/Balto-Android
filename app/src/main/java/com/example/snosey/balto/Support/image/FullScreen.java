package com.example.snosey.balto.Support.image;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.snosey.balto.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;


/**
 * Created by Snosey on 12/17/2017.
 */

public class FullScreen extends Dialog {
    public FullScreen(@NonNull final FragmentActivity activity, String url) throws JSONException {
        super(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.show();
        this.setContentView(R.layout.full_screen);
        PhotoView imageView = this.findViewById(R.id.image);
        Picasso.with(activity).load(url).into(imageView);
    }
}
