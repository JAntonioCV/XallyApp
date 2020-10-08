package com.jantonioc.ln;

import java.io.Serializable;

public class Pago implements Serializable {

    private String cliente;
    private String pago;

    public Pago() {
    }

    public Pago(String cliente, String pago) {
        this.cliente = cliente;
        this.pago = pago;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getPago() {
        return pago;
    }

    public void setPago(String pago) {
        this.pago = pago;
    }
}
