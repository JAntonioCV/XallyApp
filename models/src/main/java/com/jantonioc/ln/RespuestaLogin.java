package com.jantonioc.ln;

import android.content.SharedPreferences;

import java.io.Serializable;

public class RespuestaLogin implements Serializable {

    private int id;
    private String nombreCompleto;
    private String rol;
    private boolean exito;

    public RespuestaLogin() {
    }

    public RespuestaLogin(int id, String nombreCompleto, String rol, boolean exito) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.exito = exito;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }
}
