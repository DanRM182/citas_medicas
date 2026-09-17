package com.dan.msv.citas.controller;

import com.dan.commons.controller.CrudController;
import com.dan.msv.citas.dto.CitaRequest;
import com.dan.msv.citas.dto.CitaResponse;
import com.dan.msv.citas.service.CitaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController @Validated
@Tag(name = "API Citas", description = "Métodos para la gestión de citas")
public class CitaController extends CrudController<CitaRequest, CitaResponse, CitaService> {
    public CitaController(CitaService service) {
        super(service);
    }

    @Operation(
            summary = "Actualizar estado de la cita",
            description = "Actualiza el estado de una cita utilizando " +
                    "el identificador de la cita y el identificador del nuevo estado."
    )
    @PatchMapping("/{idCita}/estado/{idEstado}")
    public ResponseEntity<Void> actualizarEstadoCita(
            @PathVariable @Positive(message = "El idCita debe ser positivo") Long idCita,
            @PathVariable @Positive(message = "El idEstado debe ser positivo") Long idEstado
    ) {
        service.actualizarEstadoCita(idCita, idEstado);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Validar estado de cita en paciente",
            description = "Valida si el paciente tiene citas en estado CONFIRMADO o EN_CURSO."
    )
    @GetMapping("/{idPaciente}/estadoCitaPaciente")
    public ResponseEntity<Void> validarEstadoCitasPaciente(
            @PathVariable @Positive(message = "El idPaciente debe ser positivo") Long idPaciente
    ) {
        service.validarEstadoCitasDePaciente(idPaciente);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Validar estado de cita en médico",
            description = "Valida si el médico tiene citas en estado CONFIRMADO o EN_CURSO."
    )
    @GetMapping("/{idMedico}/estadoCitaMedico")
    public ResponseEntity<Void> validarEstadoCitasMedico(
            @PathVariable @Positive(message = "El idMedico debe ser positivo") Long idMedico
    ) {
        service.validarEstadoCitasDeMedico(idMedico);
        return ResponseEntity.noContent().build();
    }
}
