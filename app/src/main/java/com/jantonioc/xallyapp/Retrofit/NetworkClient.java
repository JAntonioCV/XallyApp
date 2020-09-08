package com.jantonioc.xallyapp.Retrofit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient  {

    private static Retrofit retrofit;
    private static final String BASE_URL="http://192.168.1.52/ProyectoXalli_Gentelella/";

    public static Retrofit getRetrofit()
    {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().cache(null).build();
        if(retrofit == null)
        {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        }

        return retrofit;
    }

}
