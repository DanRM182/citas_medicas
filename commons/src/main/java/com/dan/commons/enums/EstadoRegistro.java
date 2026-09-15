package com.dan.commons.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EstadoRegistro {
    ACTIVO("Activo"),
    ELIMINADO("Eliminado");

    private final String descripcion;
}

