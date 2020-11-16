package com.jantonioc.ln;

import java.io.Serializable;

public class ProductoVendido implements Serializable {

    public String nombre;
    public int cantidad;

    public ProductoVendido() {
    }

    public ProductoVendido(String nombre, int cantidad) {
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
