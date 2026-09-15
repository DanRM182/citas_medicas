package com.dan.commons.exceptions;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.dan.commons.dto.CustomErrorResponse;
import com.dan.commons.exceptions.EntidadRelacionadaException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String mensaje = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Error de validación en los datos enviados");
        log.error("Error de validación de argumentos: {}", mensaje);
        return ResponseEntity.badRequest()
                .body(new CustomErrorResponse(HttpStatus.BAD_REQUEST.value(), mensaje));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CustomErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Error en la petición: {}", e.getMessage());
        return ResponseEntity.badRequest().body(
                new CustomErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage())
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<CustomErrorResponse> handleIllegalStateException(IllegalStateException e) {
        log.error("Error en el estado de la petición: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new CustomErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage())
        );
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<CustomErrorResponse> handleNoSuchElementException(NoSuchElementException e) {
        log.warn("No se encontró recurso: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new CustomErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn("No se encontró recurso estático: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new CustomErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<CustomErrorResponse> handleRecursoNoEncontradoException(RecursoNoEncontradoException e) {
        log.warn("No se encontró el recurso: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new CustomErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(EntidadRelacionadaException.class)
    public ResponseEntity<CustomErrorResponse> handleEntidadRelacionadaException(EntidadRelacionadaException e) {
        log.warn("Error al eliminar un recurso: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new CustomErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleGeneralException(Exception e) {
        log.error("Error interno del servidor: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CustomErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Error interno del servidor. Por favor, contacte al administrador."));
    }

}
