package com.jantonioc.xalliapp;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class Constans {

    //Constante para los servicios WEB
    //public static final String URLBASE="http://proyectoxally.somee.com/";
    public static final String URLBASE="http://192.168.0.52/ProyectoXalli_Gentelella/";

    //para el nombre de usuario, id y rol
    public static int id;
    public static String nombre;
    public static String rol;
    public static boolean exito;


    //preferencias encriptadas para el login y authentication

    public static final String LLAVE_EDITOR="llave.encryptedSharedPreferences";
    private static MasterKey masterKey;
    private static EncryptedSharedPreferences sharedPreferences;
    private static final String USER="user";
    private static final String PASS="pass";

    //obtener los datos de la credenciales para el login
    public static String [] obtenerDatos(Context context)
    {
        String [] cred = new String[2];
        sharedPreferences = obtenerInstancia(context);

        cred[0] = sharedPreferences.getString(USER, "");
        cred[1] = sharedPreferences.getString(PASS, "");

        return cred;
    }

    //retorna la instania de las preferencias compartidas encriptadas
    public static EncryptedSharedPreferences obtenerInstancia(Context context)
    {
        try {
            masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
        }catch (GeneralSecurityException ex)
        {
            ex.printStackTrace();

        }catch (IOException ex)
        {
            ex.printStackTrace();
        }

        try {
            sharedPreferences = (EncryptedSharedPreferences) EncryptedSharedPreferences
                    .create(
                            context,
                            LLAVE_EDITOR,
                            masterKey,
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    );
        }catch (GeneralSecurityException ex)
        {
            ex.printStackTrace();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        return sharedPreferences;
    }

    //guardar las credenciales en las preferencias compartidas de la aplicacion
    public static boolean guardarCredenciales(Context context,String username, String password)
    {
        if(context != null && !username.isEmpty() && !password.isEmpty())
        {
            sharedPreferences = obtenerInstancia(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(USER,username);
            editor.putString(PASS,password);
            editor.apply();

            return true;
        }

        return false;
    }

    //metodo que elimina la contraseña de la preferencias compartidas encriptadas
    public static boolean eliminarpass(Context context)
    {
        if(context!=null)
        {
            sharedPreferences = obtenerInstancia(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(PASS);
            editor.apply();
            return true;
        }

        return false;
    }

}
