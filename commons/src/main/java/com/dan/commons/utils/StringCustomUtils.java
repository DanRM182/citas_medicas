package com.dan.commons.utils;

public class StringCustomUtils {
    public static void validarNoVacio(String texto, String mensaje) {
        if(texto == null || texto.isBlank())
            throw new IllegalArgumentException(mensaje);
    }

    public static void validarTamanio(String texto, Integer min, Integer max, String mensaje) {
        validarNoVacio(texto, mensaje);

        if(texto.length() < min || texto.length() > max)
            throw new IllegalArgumentException(mensaje);
    }

    public static String quitarAcentos(String texto) {
        return texto.toLowerCase()
                .replace("á", "a").replace("é", "e")
                .replace("í", "i").replace("ó", "o")
                .replace("ú", "u").replace("û", "u");
    }
}
