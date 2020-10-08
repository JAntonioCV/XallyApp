package com.jantonioc.xalliapp.Retrofit;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BasicAuthInterceptor implements Interceptor {

    //string que son las credenciales
    private String credentials;

    //se le pasa el usuario y contraseña
    public BasicAuthInterceptor(String user, String password) {
        this.credentials = Credentials.basic(user, password);
    }

    //sirve para hacer la autentificacion para el metodo de enviar la foto
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request authenticatedRequest = request.newBuilder()
                .header("Authorization", credentials).build();
        return chain.proceed(authenticatedRequest);
    }
}
