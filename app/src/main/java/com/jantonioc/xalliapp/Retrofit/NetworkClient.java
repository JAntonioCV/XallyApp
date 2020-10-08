package com.jantonioc.xalliapp.Retrofit;

import android.content.Context;

import com.jantonioc.xalliapp.Constans;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient  {

    //variables nsesarias para retrofit
    private static Retrofit retrofit;

    //obtener intancia de retorfit
    public static Retrofit getRetrofit(Context context)
    {
        //interceptador de las peticiones http
        //HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        //que va a observar el cuerpo
        //interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //para la autenticacion basica
        String [] cred = Constans.obtenerDatos(context);
        final String userName = cred[0];
        final String password = cred[1];
        BasicAuthInterceptor interceptor = new BasicAuthInterceptor(userName,password);

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
