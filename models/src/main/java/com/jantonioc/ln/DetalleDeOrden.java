package com.jantonioc.ln;

import java.io.Serializable;

public class DetalleDeOrden implements Serializable {

    private int id;
    private int cantidad;
    private String nota;
    private String nombreplatillo;
    private Boolean estado;
    private double precio;
    private int menuid;
    private Boolean fromservice;

    public DetalleDeOrden() {
    }

    public DetalleDeOrden(int id, int cantidad, String nota, String nombreplatillo, Boolean estado, double precio, int menuid, Boolean fromservice) {
        this.id = id;
        this.cantidad = cantidad;
        this.nota = nota;
        this.nombreplatillo = nombreplatillo;
        this.estado = estado;
        this.precio = precio;
        this.menuid = menuid;
        this.fromservice = fromservice;
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

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getMenuid() {
        return menuid;
    }

    public void setMenuid(int menuid) {
        this.menuid = menuid;
    }

    public Boolean getFromservice() {
        return fromservice;
    }

    public void setFromservice(Boolean fromservice) {
        this.fromservice = fromservice;
    }
}
