package com.dan.commons.dto.medicos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record MedicoRequest(
        @Schema(description = "Nombre del médico", example = "Carlos")
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
        String nombre,

        @Schema(description = "Apellido paterno del médico", example = "Ramírez")
        @NotBlank(message = "El apellido paterno es requerido")
        @Size(min = 1, max = 50, message = "El apellido paterno debe tener entre 1 y 50 caracteres")
        String apellidoPaterno,

        @Schema(description = "Apellido materno del médico", example = "López")
        @NotBlank(message = "El apellido materno es requerido")
        @Size(min = 1, max = 50, message = "El apellido materno debe tener entre 1 y 50 caracteres")
        String apellidoMaterno,

        @Schema(description = "Edad del médico en años", example = "35")
        @NotNull(message = "La edad del médico es requerida")
        @Min(value = 18, message = "La edad debe ser mínimo 18")
        @Max(value = 100, message = "La edad debe ser máximo 100")
        Short edad,

        @Schema(description = "Correo electrónico del médico", example = "carlos.ramirez@example.com")
        @NotBlank(message = "El email es requerido")
        @Email(
                regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
                message = "El formato del correo electrónico no es válido"
        )
        @Size(
                min = 8,
                max = 100,
                message = "El email debe tener entre 8 y 100 caracteres"
        )
        String email,

        @Schema(description = "Número telefónico de 10 dígitos", example = "7771234567")
        @NotBlank(message = "El teléfono es requerido")
        @Pattern(
                regexp = "^[0-9]{10}$",
                message = "El teléfono debe tener 10 dígitos"
        )
        String telefono,

        @Schema(description = "Cédula profesional del médico", example = "123456789012")
        @NotBlank(message = "La cédula profesional es requerida")
        @Size(
                min = 12,
                max = 12,
                message = "La cédula profesional debe tener exactamente 12 caracteres"
        )
        String cedulaProfesional,

        @Schema(description = "Identificador de la especialidad médica", example = "5")
        @NotNull(message = "El ID de la especialidad es requerido")
        @Positive(message = "El ID de la especialidad debe ser positivo")
        Long idEspecialidad

) { }