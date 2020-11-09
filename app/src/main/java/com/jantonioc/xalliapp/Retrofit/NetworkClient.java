package com.jantonioc.xalliapp.Retrofit;

import android.content.Context;

import com.jantonioc.xalliapp.Constans;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient  {

    //variables nsesarias para retrofit
    private static Retrofit retrofit;

    //obtener intancia de retorfit
    public static Retrofit getRetrofit(Context context)
    {
        //para la autenticacion basica
        String [] cred = Constans.obtenerDatos(context);
        final String userName = cred[0];
        final String password = cred[1];
        BasicAuthInterceptor interceptor = new BasicAuthInterceptor(userName,password);

        //cliente Okhhtpclient para la conxeion
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                        .readTimeout(15,TimeUnit.SECONDS)
                                        .addInterceptor(interceptor)
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
