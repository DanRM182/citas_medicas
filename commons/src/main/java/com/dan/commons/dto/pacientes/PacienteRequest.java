package com.dan.commons.dto.pacientes;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos necesarios para almacenar un paciente")
public record PacienteRequest(
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
        @Schema(description = "Nombre del paciente", example = "Juan Gabriel")
        String nombre,

        @NotBlank(message = "El apellido paterno es requerido")
        @Size(min = 1, max = 50, message = "El apellido paterno debe tener entre 1 y 50 caracteres")
        @Schema(description = "Apellido paterno del paciente", example = "Gónzales")
        String apellidoPaterno,

        @NotBlank(message = "El apellido materno es requerido")
        @Size(min = 1, max = 50, message = "El apellido materno debe tener entre 1 y 50 caracteres")
        @Schema(description = "Apellido materno del paciente", example = "Pérez")
        String apellidoMaterno,

        @Schema(description = "Edad del paciente", example = "1")
        @NotNull(message = "La edad del paciente es requerida")
        @Min(value = 1, message = "La edad debe ser mínimo 1")
        @Max(value = 100, message = "La edad debe ser máximo 100")
        Short edad,

        @Schema(description = "Peso del paciente", example = "85.2")
        @NotNull(message = "El peso del paciente es requerido")
        @DecimalMin(value = "0.1", message = "El peso debe ser mínimo 0.1")
        @DecimalMax(value = "200", message = "El peso debe ser máximo 200")
        Double peso,

        @Schema(description = "Estatura del paciente", example = "1.65")
        @NotNull(message = "La estatura del paciente es requerido")
        @DecimalMin(value = "1.0", message = "La estatura debe ser mínimo 1.0")
        @DecimalMax(value = "2.0", message = "La estatura debe ser máximo 2.0")
        Double estatura,

        @NotBlank(message = "El email es requerido")
        @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
                message = "El formato del correo electrónico no es válido")
        @Size(min = 8, max = 100, message = "El email debe tener entre 8 y 100 caracteres")
        @Schema(description = "Email del paciente", example = "correo@correo.com")
        String email,

        @NotBlank(message = "El telefono es requerido")
        @Pattern(regexp = "^[0-9]{10}", message = "El teléfono debe tener 10 dígitos")
        @Schema(description = "Teléfono del paciente", example = "5512345678")
        String telefono,

        @NotBlank(message = "La dirección es requerida")
        @Size(min = 1, max = 150, message = "La dirección debe tener entre 1 y 150 caracteres")
        @Schema(description = "Dirección del paciente", example = "Av Siempre Viva 512")
        String direccion
) { }