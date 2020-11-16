package com.jantonioc.ln;

import java.io.Serializable;

public class VentasMes implements Serializable {

    double totalVentas;
    int mes;

    public VentasMes() {
    }

    public VentasMes(float totalVentas, int mes) {
        this.totalVentas = totalVentas;
        this.mes = mes;
    }

    public double getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(float totalVentas) {
        this.totalVentas = totalVentas;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }
}
