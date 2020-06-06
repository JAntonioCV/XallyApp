package com.jantonioc.ln;

public class Orden {

    private int id;
    private String codigo;
    private String fechaorden;
    private String tiempoorden;
    private boolean estado;

    public Orden() {
    }

    public Orden(int id, String codigo, String fechaorden, String tiempoorden, boolean estado) {
        this.id = id;
        this.codigo = codigo;
        this.fechaorden = fechaorden;
        this.tiempoorden = tiempoorden;
        this.estado = estado;
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

    public String getFechaorden() {
        return fechaorden;
    }

    public void setFechaorden(String fechaorden) {
        this.fechaorden = fechaorden;
    }

    public String getTiempoorden() {
        return tiempoorden;
    }

    public void setTiempoorden(String tiempoorden) {
        this.tiempoorden = tiempoorden;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
