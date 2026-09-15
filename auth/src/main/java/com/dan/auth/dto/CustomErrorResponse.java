package com.dan.auth.dto;

public record CustomErrorResponse(
        int codigo,
        String mensaje
) { }
