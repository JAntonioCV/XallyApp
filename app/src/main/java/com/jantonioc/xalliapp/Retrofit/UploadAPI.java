package com.jantonioc.xalliapp.Retrofit;

import com.jantonioc.ln.RespuestaLogin;
import com.jantonioc.ln.ResultadoWS;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface UploadAPI {

    //Metodo que utliza retrofit para enviar una imagen al server
    @Multipart
    @POST("ComandaWS/addPhotoComanda")
    Call<ResultadoWS> uploadComanda(

            @Part MultipartBody.Part photo,
            @Part("idorden") RequestBody idorden
    );

    //Metodo que utliza retrofit para enviar una imagen al server
    @Multipart
    @POST("CarnetWS/addPhotoCarnet")
    Call<ResultadoWS> uploadCarnet(
            @Part MultipartBody.Part photo
    );

    //iniciar sesion
    @GET("LoginWS/Login")
    Call<RespuestaLogin> Login(
            @Query("user") String user,
            @Query("pass") String pass
    );

}
