package com.jantonioc.ln;

import java.io.Serializable;

public class ResultadoWS implements Serializable {

    private String Mensaje;
    private boolean Resultado;

    public ResultadoWS() {
    }

    public ResultadoWS(String mensaje, boolean resultado) {
        Mensaje = mensaje;
        Resultado = resultado;
    }

    public String getMensaje() {
        return Mensaje;
    }

    public void setMensaje(String mensaje) {
        Mensaje = mensaje;
    }

    public boolean isResultado() {
        return Resultado;
    }

    public void setResultado(boolean resultado) {
        Resultado = resultado;
    }
}
