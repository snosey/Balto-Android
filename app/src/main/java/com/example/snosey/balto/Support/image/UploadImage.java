package com.example.snosey.balto.Support.image;

import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.WebService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by pc on 10/21/2017.
 */

public class UploadImage {
    FragmentActivity activity;
    List<Uri> list;
    ProgressBar dialog;
    public UploadImage.AsyncResponse delegate = null;

    public interface AsyncResponse {
        void processFinish(boolean output);
    }

    public UploadImage(UploadImage.AsyncResponse asyncResponse, FragmentActivity activity, List<Uri> list) {
        this.activity = activity;
        this.delegate = asyncResponse;
        this.list = list;
        dialog = activity.findViewById(R.id.progress);
        try {
            if (dialog.getVisibility() == View.GONE) {
                dialog.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {

        }
        Log.e("image numbers:", list.size() + "");
        if (list.size() == 0) {
            dialog.setVisibility(View.GONE);
            delegate.processFinish(true);
        } else if (list.size() == 1) {
            upload(0);
        } else
            upload(1);
    }

    void upload(final int i) {
        if (i == list.size()) {
            dialog.setVisibility(View.GONE);
            delegate.processFinish(true);
            return;
        } else if (list.get(i).toString().contains(WebService.Image.imageLink)) {
            upload(i + 1);
        }
//done
        else {
            File file = new File((list.get(i).getPath()));
            ////////////////
            String imgName = getFileName(list.get(i));
            if (!(imgName.endsWith("jpg") || imgName.endsWith("png") || imgName.endsWith("jpeg"))) {
                imgName += ".png";
            }

            Log.e("imageName:", imgName);
            Log.e("imageSize:", file.length() + "");

            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", imgName, requestBody);
            RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), imgName);
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS).build();
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(WebService.Image.baseUrl).client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            ApiConfig getResponse = retrofit.create(ApiConfig.class);
            Call<ServerResponse> call = getResponse.uploadFile(fileToUpload, filename);
            call.enqueue(new retrofit2.Callback<ServerResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    upload(i + 1);
                }


                @Override
                public void onFailure(Call call, Throwable t) {
                    Toast.makeText(activity, "Please try again...", Toast.LENGTH_SHORT).show();
                    dialog.setVisibility(View.GONE);
                    delegate.processFinish(false);
                    t.printStackTrace();
                }
            });

        }
    }

    public String getFileName(Uri uri) {

        String result = null;
        try {
            if (uri.getScheme().equals("content")) {
                Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
        String currentDateandTime = sdf.format(new Date());

        if (!(result.endsWith("jpg") || result.endsWith("png") || result.endsWith("jpeg"))) {
            result += ".png";
        }

        return currentDateandTime + result;
    }


}
