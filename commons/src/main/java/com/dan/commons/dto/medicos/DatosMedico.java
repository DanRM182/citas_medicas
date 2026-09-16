package com.dan.commons.dto.medicos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de un médico asociados a una cita")
public record DatosMedico(
        @Schema(
                description = "Nombre completo del médico",
                example = "Carlos Ramírez López"
        )
        String nombre,

        @Schema(
                description = "Cédula profesional del médico",
                example = "123456789012"
        )
        String cedulaProfesional,

        @Schema(
                description = "Nombre de la especialidad médica del médico",
                example = "Cardiología"
        )
        String especialidad
) { }