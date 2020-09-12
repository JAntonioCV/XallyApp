package com.jantonioc.xallyapp.Retrofit;

import com.jantonioc.ln.Categoria;
import com.jantonioc.ln.ResultadoWS;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadAPI {

    @Headers("Cache-control: no-cache")
    @Multipart
    @POST("ComandaWS/addPhotoComanda")
    Call<ResultadoWS> uploadImage(

            @Part MultipartBody.Part photo,
            @Part("idorden") RequestBody idorden
    );

}
