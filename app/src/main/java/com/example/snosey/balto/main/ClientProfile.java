package com.example.snosey.balto.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.snosey.balto.BuildConfig;
import com.example.snosey.balto.MainActivity;
import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.image.CircleTransform;
import com.example.snosey.balto.Support.image.CompressImage;
import com.example.snosey.balto.Support.image.GetFileName;
import com.example.snosey.balto.Support.image.UploadImage;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.login.RegistrationActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import in.myinnos.awesomeimagepicker.activities.AlbumSelectActivity;
import in.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Snosey on 3/25/2018.
 */

public class ClientProfile extends Fragment {

    String newLogo = "";

    @InjectView(R.id.logo)
    ImageView logo;
    @InjectView(R.id.change)
    com.example.snosey.balto.Support.CustomTextView change;

    @InjectView(R.id.gender)
    com.example.snosey.balto.Support.CustomTextView gender;
    @InjectView(R.id.fisrtName)
    EditText fisrtName;
    @InjectView(R.id.secondName)
    EditText secondName;

    @InjectView(R.id.rate)
    com.example.snosey.balto.Support.CustomTextView rate;
    @InjectView(R.id.confirm)
    Button confirm;
    @InjectView(R.id.phone)
    com.example.snosey.balto.Support.CustomTextView phone;
    @InjectView(R.id.email)
    com.example.snosey.balto.Support.CustomTextView email;

    @InjectView(R.id.changePassword)
    com.example.snosey.balto.Support.CustomTextView changePassword;


    @InjectView(R.id.passwordLayout)
    LinearLayout passwordLayout;

    @InjectView(R.id.newPassword)
    EditText newPassword;

    @InjectView(R.id.oldPassword)
    EditText oldPassword;


    private String tempOldPassword = "";

    @InjectView(R.id.reviews)
    com.example.snosey.balto.Support.CustomTextView reviews;

    JSONArray rateJsonArray;
    RateAdapter rateAdapter;
    RecyclerView rateRV;


    List<String> deleteImages, addImages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.client_profile, container, false);
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.back)).setVisibility(View.VISIBLE);
        ((RelativeLayout) getActivity().getWindow().getDecorView().findViewById(R.id.menuHome)).setVisibility(View.GONE);
        ButterKnife.inject(this, view);

        oldPassword.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (oldPassword.getVisibility() == View.GONE) {
                    oldPassword.setVisibility(View.VISIBLE);
                    newPassword.setVisibility(View.VISIBLE);
                } else {
                    oldPassword.setVisibility(View.GONE);
                    newPassword.setVisibility(View.GONE);
                }

            }
        });

        rateJsonArray = new JSONArray();
        rateAdapter = new RateAdapter();
        rateRV = (RecyclerView) view.findViewById(R.id.rateRV);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rateRV.setLayoutManager(layoutManager);
        rateRV.setAdapter(rateAdapter);
        setRate(getArguments().getString(WebService.Booking.id_user));
        ///////////////////////////////////////
        deleteImages = new ArrayList<>();
        addImages = new ArrayList<>();
        ///////////////////////////////////////

        checkOwner(getArguments().getString(WebService.Slider.id_user));

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AlbumSelectActivity.class);
                try {
                    intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, 1); // set limit for image selection
                    startActivityForResult(intent, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    private void setRate(String id_user) {
        UrlData urlData = new UrlData();
        urlData.add(WebService.Booking.id_user, id_user);
        urlData.add(WebService.Booking.type, WebService.Booking.client);
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                rateJsonArray = new JSONObject(output).getJSONArray("reviews");
                rateAdapter.notifyDataSetChanged();
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Setting.reviewApi, urlData.get());
    }


    private class RateAdapter extends RecyclerView.Adapter<MyViewHolderRate> {

        @Override
        public MyViewHolderRate onCreateViewHolder(ViewGroup parent, final int position) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rate_row, parent, false);
            return new MyViewHolderRate(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolderRate holder, final int position) {
            try {
                JSONObject rateObject = rateJsonArray.getJSONObject(position);
                holder.rate.setText(rateObject.getString(WebService.Booking.rate));
                holder.review.setText(rateObject.getString(WebService.Booking.review));
                if (rateObject.getJSONObject("user").toString().contains("null"))
                    return;

                holder.firstName.setText(rateObject.getJSONObject("user").getString(WebService.SignUp.first_name_en));
                if (!rateObject.getJSONObject("user").getString(WebService.SignUp.image).equals("")) {
                    String imageLink = rateObject.getJSONObject("user").getString(WebService.SignUp.image);
                    if (!imageLink.startsWith("https://"))
                        imageLink = WebService.Image.fullPathImage + imageLink;
                    Picasso.with(getActivity()).load(imageLink).transform(new CircleTransform()).into(holder.logo);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return (int) rateJsonArray.length();
        }
    }

    public class MyViewHolderRate extends RecyclerView.ViewHolder {

        public com.example.snosey.balto.Support.CustomTextView rate, firstName, review;
        public ImageView logo;

        public MyViewHolderRate(View v) {
            super(v);
            firstName = (com.example.snosey.balto.Support.CustomTextView) v.findViewById(R.id.firstName);
            review = (com.example.snosey.balto.Support.CustomTextView) v.findViewById(R.id.review);
            rate = (com.example.snosey.balto.Support.CustomTextView) v.findViewById(R.id.rate);
            logo = (ImageView) v.findViewById(R.id.logo);

            Typeface font = Typeface.createFromAsset(
                    getActivity().getAssets(),
                    "fonts/arial.ttf");
            firstName.setTypeface(font, Typeface.BOLD);
            rate.setTypeface(font, Typeface.BOLD);
        }
    }

    private void checkOwner(String id_user) {
        if (BuildConfig.APPLICATION_ID.contains("doctor")) {
            reviews.setVisibility(View.VISIBLE);
            rateRV.setVisibility(View.VISIBLE);
        }

        try {
            if (!MainActivity.jsonObject.getString("id").equals(id_user)) {
                email.setVisibility(View.GONE);
                passwordLayout.setVisibility(View.GONE);
                phone.setVisibility(View.GONE);
                confirm.setVisibility(View.GONE);
                change.setVisibility(View.GONE);
                fisrtName.setEnabled(false);
                secondName.setEnabled(false);
                setData(id_user, WebService.Booking.doctor);
            } else {
                rate.setVisibility(View.GONE);
                setData(id_user, WebService.Booking.client);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ;
    }

    private void setData(String id_user, final String type) {

        String requestApi = "";
        UrlData urlData = new UrlData();
        urlData.add(WebService.Booking.type, RegistrationActivity.sharedPreferences.getString("lang", "en"));
        urlData.add(WebService.Booking.id_client, id_user);
        requestApi = WebService.Setting.getClientApi;


        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                JSONObject user = new JSONObject(output).getJSONArray("users").optJSONObject(0);

                if (!user.getString("image").equals("")) {
                    String imageLink = user.getString("image");
                    if (!imageLink.startsWith("https://"))
                        imageLink = WebService.Image.fullPathImage + imageLink;
                    Picasso.with(getActivity()).load(imageLink).transform(new CircleTransform()).into(logo);
                }

                gender.setText(user.getString(WebService.Setting.genderName));
                fisrtName.setText(user.getString(WebService.Booking.firstName));
                secondName.setText(user.getString(WebService.Booking.lastName));
                phone.setText(user.getString(WebService.Booking.phone));
                tempOldPassword = user.getString(WebService.SignUp.password);
                if (user.getString(WebService.SignUp.email).equals("")) {
                    email.setText("facebook");
                    passwordLayout.setVisibility(View.GONE);
                    email.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.com_facebook_button_icon_blue, 0, 0, 0);
                } else {
                    email.setText(user.getString(WebService.SignUp.email));
                }

                if (!user.getString(WebService.Slider.total_rate).equals("0") &&
                        !user.getString(WebService.Slider.total_rate).equals("null"))
                    rate.setText(user.getString(WebService.Slider.total_rate));

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateUser();
                    }
                });
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, requestApi, urlData.get());
    }

    private void updateUser() {
        UrlData urlData = new UrlData();
        urlData.add(WebService.Booking.id, getArguments().getString(WebService.Booking.id_user));
        urlData.add(WebService.SignUp.first_name_ar, fisrtName.getText().toString());
        urlData.add(WebService.SignUp.first_name_en, fisrtName.getText().toString());
        urlData.add(WebService.SignUp.last_name_ar, secondName.getText().toString());
        urlData.add(WebService.SignUp.last_name_en, secondName.getText().toString());

        if (newPassword.getText().toString().length() != 0) {

            if (!oldPassword.getText().toString().equals(tempOldPassword)) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.WrongPassword), Toast.LENGTH_SHORT).show();
                return;
            }

            urlData.add(WebService.SignUp.password, newPassword.getText().toString());
        }
        if (!newLogo.equals("")) {
            List<Uri> uris = new ArrayList<>();
            uris.add(Uri.parse(newLogo));
            new UploadImage(new UploadImage.AsyncResponse() {
                @Override
                public void processFinish(boolean output) {

                }
            }, getActivity(), uris);
            newLogo = new GetFileName(uris.get(0), getActivity()).FileName();
            urlData.add(WebService.SignUp.image, newLogo);
        }
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                if (newPassword.getText().toString().length() != 0) {
                    SharedPreferences.Editor editor = RegistrationActivity.sharedPreferences.edit();
                    editor.putString("password", newPassword.getText().toString());
                    editor.commit();
                }
                if (addImages.size() == 0) {
                    getActivity().startActivity(new Intent(getActivity(), RegistrationActivity.class));
                    getActivity().finish();
                }
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Setting.updateUserApi, urlData.get());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            ArrayList<in.myinnos.awesomeimagepicker.models.Image> images = data.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES);
            CompressImage compressImage = new CompressImage();
            for (int i = 0; i < images.size(); i++) {
                Uri uri = Uri.fromFile(compressImage.compress(new File(images.get(i).path)));
                newLogo = uri.toString();
                Picasso.with(getActivity()).load(newLogo).transform(new CircleTransform()).into(logo);
            }

        }
    }


}
