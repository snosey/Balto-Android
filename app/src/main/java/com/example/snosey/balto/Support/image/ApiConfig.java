package com.example.snosey.balto.Support.image;

import com.example.snosey.balto.Support.webservice.WebService;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by pc on 10/13/2017.
 */
public interface ApiConfig {
    @Multipart
    @POST(WebService.Image.apiConfig)
    Call<ServerResponse> uploadFile(@Part MultipartBody.Part file, @Part("name") RequestBody name);
}
