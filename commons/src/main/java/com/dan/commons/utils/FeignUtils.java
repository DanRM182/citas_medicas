package com.dan.commons.utils;

import com.dan.commons.exceptions.RecursoNoEncontradoException;
import feign.FeignException;

import java.util.function.Function;

public class FeignUtils {
    public static <T, R> R validarObjetoRecibido(T objeto, Function<T, R> obtenerObjeto, String mensaje) {
        try {
            return obtenerObjeto.apply(objeto);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException(mensaje);
        }
    }
}
