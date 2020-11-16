package com.jantonioc.xalliapp.Retrofit;

import com.jantonioc.xalliapp.Constans;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BasicAuthInterceptor implements Interceptor {

    //se le pasa el usuario y contraseña
    public BasicAuthInterceptor() {

    }

    //sirve para hacer la autentificacion para el metodo de enviar la foto
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request authenticatedRequest = request.newBuilder()
                .header("Authorization", Constans.getTokenR()).build();
        return chain.proceed(authenticatedRequest);
    }
}
