package com.jantonioc.ln;

import java.io.Serializable;

public class Orden implements Serializable {

    private int id;
    private int codigo;
    private String fechaorden;
    private String tiempoorden;
    private int estado;
    private int idcliente;
    private int idmesero;
    private int idmesa;
    private String cliente;
    private String mesero;
    private String mesa;

    public Orden() {
    }

    public Orden(int id, int codigo, String fechaorden, String tiempoorden, int estado, int idcliente, int idmesero, int idmesa, String cliente, String mesero, String mesa) {
        this.id = id;
        this.codigo = codigo;
        this.fechaorden = fechaorden;
        this.tiempoorden = tiempoorden;
        this.estado = estado;
        this.idcliente = idcliente;
        this.idmesero = idmesero;
        this.idmesa = idmesa;
        this.cliente = cliente;
        this.mesero = mesero;
        this.mesa = mesa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
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

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getIdcliente() {
        return idcliente;
    }

    public void setIdcliente(int idcliente) {
        this.idcliente = idcliente;
    }

    public int getIdmesero() {
        return idmesero;
    }

    public void setIdmesero(int idmesero) {
        this.idmesero = idmesero;
    }

    public int getIdmesa() {
        return idmesa;
    }

    public void setIdmesa(int idmesa) {
        this.idmesa = idmesa;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getMesero() {
        return mesero;
    }

    public void setMesero(String mesero) {
        this.mesero = mesero;
    }

    public String getMesa() {
        return mesa;
    }

    public void setMesa(String mesa) {
        this.mesa = mesa;
    }
}


