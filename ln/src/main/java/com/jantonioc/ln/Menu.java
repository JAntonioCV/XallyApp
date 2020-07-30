package com.jantonioc.ln;

import java.io.Serializable;

public class Menu implements Serializable {

    private int id;
    private String codigo;
    private String descripcion;
    private String tiempoestimado;
    private double precio;
    private boolean estado;
    private int idcategoria;

    public Menu() {
    }

    public Menu(int id, String codigo, String descripcion, String tiempoestimado, double precio, boolean estado, int idcategoria) {
        this.id = id;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.tiempoestimado = tiempoestimado;
        this.precio = precio;
        this.estado = estado;
        this.idcategoria = idcategoria;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTiempoestimado() {
        return tiempoestimado;
    }

    public void setTiempoestimado(String tiempoestimado) {
        this.tiempoestimado = tiempoestimado;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public int getIdcategoria() {
        return idcategoria;
    }

    public void setIdcategoria(int idcategoria) {
        this.idcategoria = idcategoria;
    }
}
