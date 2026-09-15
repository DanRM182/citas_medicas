package com.dan.commons.dto.medicos;
import io.swagger.v3.oas.annotations.media.Schema;

public record MedicoResponse(

        @Schema(
                description = "Identificador único del médico",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Nombre completo del médico",
                example = "Carlos Ramírez López"
        )
        String nombre,

        @Schema(
                description = "Edad del médico en años",
                example = "35"
        )
        Short edad,

        @Schema(
                description = "Correo electrónico del médico",
                example = "carlos.ramirez@example.com"
        )
        String email,

        @Schema(
                description = "Número telefónico del médico",
                example = "7771234567"
        )
        String telefono,

        @Schema(
                description = "Cédula profesional del médico",
                example = "123456789012"
        )
        String cedulaProfesional,

        @Schema(
                description = "Nombre de la especialidad médica",
                example = "Cardiología"
        )
        String especialidad,

        @Schema(
                description = "Horario de disponibilidad del médico",
                example = "Lunes a Viernes de 08:00 a 14:00"
        )
        String disponibilidadMedico,

        @Schema(
                description = "Identificador de la disponibilidad asociada al médico",
                example = "10"
        )
        Long idDisponibilidad

) { }