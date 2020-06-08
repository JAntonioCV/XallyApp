package com.jantonioc.ln;

import java.io.Serializable;

public class DetalleDeOrden implements Serializable {

    private int id;
    private int cantidad;
    private String nota;
    private String nombreplatillo;
    private double precio;
    private int menuid;

    public DetalleDeOrden() {
    }

    public DetalleDeOrden(int id, int cantidad, String nota, String nombreplatillo, int ordenid, int menuid) {
        this.id = id;
        this.cantidad = cantidad;
        this.nota = nota;
        this.nombreplatillo = nombreplatillo;
        this.menuid = menuid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getNombreplatillo() {
        return nombreplatillo;
    }

    public void setNombreplatillo(String nombreplatillo) {
        this.nombreplatillo = nombreplatillo;
    }

    public double getPrecio() { return precio; }

    public void setPrecio(double precio) { this.precio = precio; }

    public int getMenuid() {
        return menuid;
    }

    public void setMenuid(int menuid) {
        this.menuid = menuid;
    }
}
