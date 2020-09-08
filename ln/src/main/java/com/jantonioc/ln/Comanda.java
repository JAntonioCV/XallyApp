package com.jantonioc.ln;

import java.io.Serializable;

public class Comanda implements Serializable {

    private int idorden;
    private String nombrecompleto;

    public Comanda() {
    }

    public Comanda(int idorden, String nombrecompleto) {
        this.idorden = idorden;
        this.nombrecompleto = nombrecompleto;
    }

    public int getIdorden() {
        return idorden;
    }

    public void setIdorden(int idorden) {
        this.idorden = idorden;
    }

    public String getNombrecompleto() {
        return nombrecompleto;
    }

    public void setNombrecompleto(String nombrecompleto) {
        this.nombrecompleto = nombrecompleto;
    }
}
