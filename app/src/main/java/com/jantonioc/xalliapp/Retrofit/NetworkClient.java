package com.jantonioc.xalliapp.Retrofit;

import android.content.Context;

import com.jantonioc.xalliapp.Constans;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient  {

    //variables nsesarias para retrofit
    private static Retrofit retrofit;

    //obtener intancia de retorfit
    public static Retrofit getRetrofit()
    {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


        //obtener la autenticacion basica
        BasicAuthInterceptor authInterceptor = new BasicAuthInterceptor();

        //cliente Okhhtpclient para la conxeion
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                        .readTimeout(15,TimeUnit.SECONDS)
                                        .addInterceptor(authInterceptor)
                                        //.addInterceptor(interceptor)
                                        .build();
        if(retrofit == null)
        {
            //crea la intacia y el servicio
            retrofit = new Retrofit.Builder()
                            .baseUrl(Constans.URLBASE)
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(okHttpClient)
                            .build();
        }

        //retorna retrofit con los valores
        return retrofit;
    }

}
