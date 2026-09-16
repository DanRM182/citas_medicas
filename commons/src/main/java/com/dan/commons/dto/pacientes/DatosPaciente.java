package com.dan.commons.dto.pacientes;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de un paciente asociado a una cita")
public record DatosPaciente(
        @Schema(description = "Nombre completo del paciente", example = "Máximo Décimo Meridio")
        String nombre,

        @Schema(description = "Número de expediente del paciente", example = "5X5X7X2X7X2X7X0X4X")
        String numExpediente,

        @Schema(description = "Edad del paciente", example = "1")
        String edad,

        @Schema(description = "Peso del paciente", example = "75.5")
        String peso,

        @Schema(description = "Estatura del paciente", example = "1.72")
        String estatura,

        @Schema(description = "IMC del paciente", example = "26.72")
        String imc,

        @Schema(description = "Teléfono del paciente", example = "5512345678")
        String telefono
) { }