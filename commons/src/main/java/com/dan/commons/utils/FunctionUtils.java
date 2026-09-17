package com.dan.commons.utils;

import com.dan.commons.exceptions.EntidadRelacionadaException;
import com.dan.commons.exceptions.RecursoNoEncontradoException;
import feign.FeignException;

import java.util.function.Consumer;
import java.util.function.Function;

public class FunctionUtils {
    public static void validarEstadoCitas(Long id, Consumer<Long> validarCitas, String mensaje) {
        try {
            validarCitas.accept(id);
        } catch (FeignException.Conflict e) {
            throw new EntidadRelacionadaException(mensaje);
        }
    }
}
