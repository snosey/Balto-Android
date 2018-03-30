package com.example.snosey.balto.main;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.snosey.balto.BuildConfig;
import com.example.snosey.balto.MainActivity;
import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.image.CircleTransform;
import com.example.snosey.balto.Support.image.CompressImage;
import com.example.snosey.balto.Support.image.FullScreen;
import com.example.snosey.balto.Support.image.GetFileName;
import com.example.snosey.balto.Support.image.UploadImage;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import in.myinnos.awesomeimagepicker.activities.AlbumSelectActivity;
import in.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Snosey on 3/25/2018.
 */

public class Profile extends Fragment {

    String newLogo = "";

    @InjectView(R.id.logo)
    ImageView logo;
    @InjectView(R.id.change)
    TextView change;
    @InjectView(R.id.gender)
    TextView gender;
    @InjectView(R.id.fisrtName)
    EditText fisrtName;
    @InjectView(R.id.secondName)
    EditText secondName;
    @InjectView(R.id.type)
    TextView doctorKind;
    @InjectView(R.id.rate)
    TextView rate;
    @InjectView(R.id.confirm)
    Button confirm;
    @InjectView(R.id.newCer)
    Button addCer;
    @InjectView(R.id.phone)
    TextView phone;
    @InjectView(R.id.email)
    TextView email;
    @InjectView(R.id.certification)
    TextView certification;

    JSONArray rateJsonArray;
    RateAdapter rateAdapter;
    RecyclerView rateRV;


    JSONArray cerJsonArray;
    CerAdapter cerAdapter;
    RecyclerView cerRV;


    JSONArray cerJsonArrayNew;
    CerAdapterNew cerAdapterNew;
    RecyclerView cerRVNew;

    List<String> deleteImages, addImages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile, container, false);
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.back)).setVisibility(View.VISIBLE);
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.menu)).setVisibility(View.GONE);
        ButterKnife.inject(this, view);

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
        cerJsonArray = new JSONArray();
        cerAdapter = new CerAdapter();
        cerRV = (RecyclerView) view.findViewById(R.id.cerRV);
        LinearLayoutManager layoutManager2
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        cerRV.setLayoutManager(layoutManager2);
        cerRV.setAdapter(cerAdapter);
        ///////////////////////////////////////
        cerJsonArrayNew = new JSONArray();
        cerAdapterNew = new CerAdapterNew();
        cerRVNew = (RecyclerView) view.findViewById(R.id.newCerRV);
        LinearLayoutManager layoutManagerNew
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        cerRVNew.setLayoutManager(layoutManagerNew);
        cerRVNew.setAdapter(cerAdapterNew);

        checkOwner(getArguments().getString(WebService.Slider.id_user));

        addCer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AlbumSelectActivity.class);
                try {
                    intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, 10); // set limit for image selection
                    startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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

        public TextView rate, firstName, review;
        public ImageView logo;

        public MyViewHolderRate(View v) {
            super(v);
            firstName = (TextView) v.findViewById(R.id.firstName);
            review = (TextView) v.findViewById(R.id.review);
            rate = (TextView) v.findViewById(R.id.rate);
            logo = (ImageView) v.findViewById(R.id.logo);

            Typeface font = Typeface.createFromAsset(
                    getActivity().getAssets(),
                    "fonts/arial.ttf");
            firstName.setTypeface(font, Typeface.BOLD);
            rate.setTypeface(font, Typeface.BOLD);
        }
    }


    private class CerAdapter extends RecyclerView.Adapter<MyViewHolderCer> {

        @Override
        public MyViewHolderCer onCreateViewHolder(ViewGroup parent, final int position) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.certification_row, parent, false);
            return new MyViewHolderCer(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolderCer holder, final int position) {
            try {
                final JSONObject cerObject = cerJsonArray.getJSONObject(position);
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            deleteImages.add(cerObject.getString("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        cerJsonArray.remove(position);
                        cerAdapter.notifyDataSetChanged();
                    }
                });
                Picasso.with(getActivity()).load(WebService.Image.fullPathImage + cerObject.getString("image")).fit().centerCrop().into(holder.image);
                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            new FullScreen(getActivity(), WebService.Image.fullPathImage + cerObject.getString("image"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return (int) cerJsonArray.length();
        }
    }

    private class CerAdapterNew extends RecyclerView.Adapter<MyViewHolderCer> {

        @Override
        public MyViewHolderCer onCreateViewHolder(ViewGroup parent, final int position) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.certification_row, parent, false);
            return new MyViewHolderCer(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolderCer holder, final int position) {
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addImages.remove(position);
                    cerAdapterNew.notifyDataSetChanged();
                }
            });
            Picasso.with(getActivity()).load(addImages.get(position)).fit().centerCrop().into(holder.image);
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        new FullScreen(getActivity(), addImages.get(position));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return (int) addImages.size();
        }
    }

    public class MyViewHolderCer extends RecyclerView.ViewHolder {
        public ImageView image, delete;

        public MyViewHolderCer(View v) {
            super(v);
            delete = (ImageView) v.findViewById(R.id.delete);
            image = (ImageView) v.findViewById(R.id.image);
            try {
                if (MainActivity.jsonObject.getString("id").equals(getArguments().getString(WebService.Booking.id_user)))
                    delete.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    private void checkOwner(String id_user) {


        try {
            if (!MainActivity.jsonObject.getString("id").equals(id_user)) {
                email.setVisibility(View.GONE);
                phone.setVisibility(View.GONE);
                confirm.setVisibility(View.GONE);
                change.setVisibility(View.GONE);
                fisrtName.setEnabled(false);
                secondName.setEnabled(false);
                addCer.setVisibility(View.GONE);
                cerRVNew.setVisibility(View.GONE);

                if (BuildConfig.APPLICATION_ID.contains("doctor")) {
                    cerRV.setVisibility(View.GONE);
                    cerRVNew.setVisibility(View.GONE);
                    certification.setVisibility(View.GONE);
                    doctorKind.setVisibility(View.GONE);
                    setData(id_user, WebService.Booking.client);
                } else {
                    setData(id_user, WebService.Booking.doctor);
                }
            } else {
                if (BuildConfig.APPLICATION_ID.contains("doctor")) {
                    setData(id_user, WebService.Booking.doctor);
                } else {
                    cerRV.setVisibility(View.GONE);
                    certification.setVisibility(View.GONE);
                    doctorKind.setVisibility(View.GONE);
                    setData(id_user, WebService.Booking.client);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ;
    }

    private void setData(String id_user, final String type) {

        String requestApi = "";
        UrlData urlData = new UrlData();
        urlData.add(WebService.Booking.type, Locale.getDefault().getLanguage());
        if (type.equals(WebService.Booking.doctor)) {
            urlData.add(WebService.Booking.id_doctor, id_user);
            requestApi = WebService.Setting.getDoctorApi;
        } else {
            urlData.add(WebService.Booking.id_client, id_user);
            requestApi = WebService.Setting.getClientApi;
        }


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
                if (user.getString(WebService.SignUp.email).equals("")) {
                    email.setText("facebook");
                    email.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.com_facebook_button_icon_blue, 0, 0, 0);
                } else
                    email.setText(user.getString(WebService.SignUp.email));

                if (!user.getString(WebService.Slider.total_rate).equals("0") &&
                        !user.getString(WebService.Slider.total_rate).equals("null"))
                    rate.setText(user.getString(WebService.Slider.total_rate));

                if (type.equals(WebService.Booking.doctor)) {

                    cerJsonArray = user.getJSONArray("doctor_documents");
                    cerAdapter.notifyDataSetChanged();
                    if (cerJsonArray.length() == 0)
                        cerRV.setVisibility(View.GONE);

                    if (user.getJSONArray("doctor_sub").length() == 1)
                        doctorKind.setText(user.getJSONArray("doctor_sub").getJSONObject(0)
                                .getString(WebService.Setting.subName));
                    else
                        doctorKind.setText(user.getJSONArray("doctor_sub").getJSONObject(0)
                                .getString(WebService.Setting.mainName));
                }
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

        if (addImages.size() != 0) {
            final List<Uri> uris = new ArrayList<>();
            for (int i = 0; i < addImages.size(); i++)
                uris.add(Uri.parse(addImages.get(i)));
            new UploadImage(new UploadImage.AsyncResponse() {
                @Override
                public void processFinish(boolean output) {
                    addImages(uris);
                }
            }, getActivity(), uris);

        }
        if (deleteImages.size() != 0) {
            deleteImages();
        }

        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                if (addImages.size() == 0)
                    getActivity().onBackPressed();
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Setting.updateUserApi, urlData.get());

    }

    private void addImages(List<Uri> uris) {
        UrlData urlData = new UrlData();
        urlData.add(WebService.Booking.id_user, getArguments().getString(WebService.Booking.id_user));
        for (int i = 0; i < uris.size(); i++) {
            urlData.add(WebService.SignUp.image + "[]", new GetFileName(uris.get(i), getActivity()).FileName());
        }
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                getActivity().onBackPressed();
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Setting.addCer, urlData.get());
    }

    private void deleteImages() {
        UrlData urlData = new UrlData();
        for (int i = 0; i < deleteImages.size(); i++) {
            urlData.add(WebService.Booking.id, deleteImages.get(i));
            new GetData(new GetData.AsyncResponse() {
                @Override
                public void processFinish(String output) throws JSONException {

                }
            }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Setting.deleteCer, urlData.get());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantsCustomGallery.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            ArrayList<in.myinnos.awesomeimagepicker.models.Image> images = data.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES);
            CompressImage compressImage = new CompressImage();
            for (int i = 0; i < images.size(); i++) {
                Uri uri = Uri.fromFile(compressImage.compress(new File(images.get(i).path)));
                addImages.add(uri.toString());
            }
            cerAdapterNew.notifyDataSetChanged();
        } else if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            ArrayList<in.myinnos.awesomeimagepicker.models.Image> images = data.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES);
            CompressImage compressImage = new CompressImage();
            for (int i = 0; i < images.size(); i++) {
                Uri uri = Uri.fromFile(compressImage.compress(new File(images.get(i).path)));
                newLogo = uri.toString();
                Picasso.with(getActivity()).load(newLogo).transform(new CircleTransform()).into(logo);
            }
            cerAdapterNew.notifyDataSetChanged();
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String currentDateandTime = sdf.format(new Date());

        if (!(result.endsWith("jpg") || result.endsWith("png") || result.endsWith("jpeg"))) {
            result += ".png";
        }

        return currentDateandTime + result;
    }

}
