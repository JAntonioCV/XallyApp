package com.jantonioc.xalliapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.jantonioc.ln.RespuestaLogin;
import com.jantonioc.xalliapp.Retrofit.ErrorHandlingAdapter;
import com.jantonioc.xalliapp.Retrofit.NetworkClient;
import com.jantonioc.xalliapp.Retrofit.UploadAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    //interfaz de usuario
    private Button btniniciar;
    private TextInputLayout txtusuario;
    private TextInputLayout txtcontraseña;
    ProgressBar progressBar;
    ImageView imageView;

    //preferencias encriptadas
    EncryptedSharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        txtusuario = findViewById(R.id.Usuario);
        txtcontraseña = findViewById(R.id.contraseña);
        btniniciar = findViewById(R.id.iniciar);
        imageView = findViewById(R.id.logo);

        //arreglo de string para las credenciales
        String [] cred = Constans.obtenerDatos(getApplicationContext());

        //obtenemos las credenciales
        final String userName = cred[0];
        final String password = cred[1];

        //si no estan vacios inicia la sesion
        if(!userName.isEmpty() && !password.isEmpty())
        {
            //ocultamos las vistas de la interfaz
            imageView.setVisibility(View.GONE);
            txtusuario.setVisibility(View.GONE);
            txtcontraseña.setVisibility(View.GONE);
            btniniciar.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            //mandamos el contexto para que se puedan obbtener la autenticacion del cliente
            Retrofit retrofit = NetworkClient.getRetrofit(getApplicationContext());
            UploadAPI uploadAPI = retrofit.create(UploadAPI.class);
            //utiliza el servicio login para iniciar sesion
            uploadAPI.Login(userName,password).enqueue(new Callback<RespuestaLogin>() {
                @Override
                public void onResponse(Call<RespuestaLogin> call, Response<RespuestaLogin> response) {
                    //si la peticion es exitosa
                    if(response.isSuccessful())
                    {
                        //si es true
                        if(response.body().isExito())
                        {
                            //guardamos las credenciales
                            if(!Constans.guardarCredenciales(getApplicationContext(),userName,password));
                            {
                                //si da error al guardar
                                Toast.makeText(getApplicationContext(),"Error al almacenar las credenciales",Toast.LENGTH_SHORT);
                            }

                            Constans.setToken(getApplicationContext());

                            //obtener login
                            RespuestaLogin servicio = response.body();

                            //las contasntes
                            Constans.id = servicio.getId();
                            Constans.nombre = servicio.getNombreCompleto();
                            Constans.rol = servicio.getRol();
                            Constans.exito = servicio.isExito();

                            Toast.makeText(LoginActivity.this, "Bienvenido " + servicio.getNombreCompleto() + "!!", Toast.LENGTH_SHORT).show();

                            //abrir el main
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            LoginActivity.this.finish();
                            startActivity(i);
                        }
                        else
                        {
                            //si da error mostrar el error y mostrar las vistas
                            progressBar.setVisibility(View.GONE);
                            imageView.setVisibility(View.VISIBLE);
                            txtusuario.setVisibility(View.VISIBLE);
                            txtcontraseña.setVisibility(View.VISIBLE);
                            btniniciar.setVisibility(View.VISIBLE);
                            Toast.makeText(LoginActivity.this,response.body().getNombreCompleto(),Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<RespuestaLogin> call, Throwable t) {
                    //si da error mostrar el error y mostrar las vistas
                    progressBar.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    txtusuario.setVisibility(View.VISIBLE);
                    txtcontraseña.setVisibility(View.VISIBLE);
                    btniniciar.setVisibility(View.VISIBLE);
                    Toast.makeText(LoginActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        //mandamos el nombre de usuario
        txtusuario.getEditText().setText(userName);

        //iniciar sesion
        btniniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //valida si no loguea
                if(!validarCampos())
                {
                    return;
                }
                else
                {
                    loguearse();
                }
            }
        });
    }

    private void loguearse()
    {
        //ocultamos las vistas de la interfaz
        txtusuario.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        txtcontraseña.setVisibility(View.GONE);
        btniniciar.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        //mandamos el contexto para que se puedan obbtener la autenticacion del cliente
        Retrofit retrofit = NetworkClient.getRetrofit(getApplicationContext());
        UploadAPI uploadAPI = retrofit.create(UploadAPI.class);

        //utiliza el servicio login para iniciar sesion
        uploadAPI.Login(txtusuario.getEditText().getText().toString().trim(),txtcontraseña.getEditText().getText().toString().trim()).enqueue(new Callback<RespuestaLogin>() {
            @Override
            public void onResponse(Call<RespuestaLogin> call, Response<RespuestaLogin> response) {

                //si la peticion es exitosa
                if(response.isSuccessful())
                {
                    //si es true
                    if(response.body().isExito())
                    {
                        //guardamos las credenciales
                        if(!Constans.guardarCredenciales(getApplicationContext(),txtusuario.getEditText().getText().toString().trim(),txtcontraseña.getEditText().getText().toString().trim()));
                        {
                            //si da error al guardar
                            Toast.makeText(getApplicationContext(),"Error al almacenar las credenciales",Toast.LENGTH_SHORT);
                        }

                        Constans.setToken(getApplicationContext());

                        //obtener login
                        RespuestaLogin servicio = response.body();

                        //las contasntes
                        Constans.id = servicio.getId();
                        Constans.nombre = servicio.getNombreCompleto();
                        Constans.rol = servicio.getRol();
                        Constans.exito = servicio.isExito();

                        Toast.makeText(LoginActivity.this, "Bienvenido " + servicio.getNombreCompleto() + "!!", Toast.LENGTH_SHORT).show();

                        //abrir el main
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        LoginActivity.this.finish();
                        startActivity(i);
                    }
                    else
                    {
                        //si da error mostrar el error y mostrar las vistas
                        progressBar.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        txtusuario.setVisibility(View.VISIBLE);
                        txtcontraseña.setVisibility(View.VISIBLE);
                        btniniciar.setVisibility(View.VISIBLE);

                        Toast.makeText(LoginActivity.this,response.body().getNombreCompleto(),Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RespuestaLogin> call, Throwable t) {

                //si da error mostrar el error y mostrar las vistas
                progressBar.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                txtusuario.setVisibility(View.VISIBLE);
                txtcontraseña.setVisibility(View.VISIBLE);
                btniniciar.setVisibility(View.VISIBLE);
                Toast.makeText(LoginActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Validando que no esten los campos vacios
    private boolean validarCampos() {
        boolean isValidate = true;

        String usuarioInput = txtusuario.getEditText().getText().toString().trim();
        String contraseñaInput = txtcontraseña.getEditText().getText().toString().trim();

        if (usuarioInput.isEmpty()) {
            isValidate = false;
            txtusuario.setError("Ingrese un nombre de usuario");

        } else
        {
            txtusuario.setError(null);
        }

        if(contraseñaInput.isEmpty())
        {
            isValidate = false;
            txtcontraseña.setError("Ingrese una contraseña");
        }
        else
        {
            txtcontraseña.setError(null);
        }

        return isValidate;
    }


}
