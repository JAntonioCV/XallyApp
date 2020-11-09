package com.jantonioc.ln;

import java.io.Serializable;

public class Receta implements Serializable {

    private int id;
    private String descripcion;
    private String tiempoEstimado;
    private boolean estado;
    private String ingrediente;

    public Receta() {
    }

    public Receta(int id, String descripcion, String tiempoEstimado, boolean estado, String ingrediente) {
        this.id = id;
        this.descripcion = descripcion;
        this.tiempoEstimado = tiempoEstimado;
        this.estado = estado;
        this.ingrediente = ingrediente;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTiempoEstimado() {
        return tiempoEstimado;
    }

    public void setTiempoEstimado(String tiempoEstimado) {
        this.tiempoEstimado = tiempoEstimado;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getIngrediente() {
        return ingrediente;
    }

    public void setIngrediente(String ingrediente) {
        this.ingrediente = ingrediente;
    }
}
