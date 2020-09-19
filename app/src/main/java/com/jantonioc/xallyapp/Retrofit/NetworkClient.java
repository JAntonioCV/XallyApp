package com.jantonioc.xallyapp.Retrofit;

import com.jantonioc.xallyapp.Constans;

import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient  {

    //variables nsesarias para retrofit
    private static Retrofit retrofit;

    //obtener intancia de retorfit
    public static Retrofit getRetrofit()
    {
        //interceptador de las peticiones http
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        //que va a observar el cuerpo
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //cliente Okhhtpclient para la conxeion
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        if(retrofit == null)
        {
            //crea la intacia y el servicio
            retrofit = new Retrofit.Builder().baseUrl(Constans.URLBASE).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        }

        //retorna retrofit con los valores
        return retrofit;
    }

}
