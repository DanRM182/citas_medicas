package com.dan.msv.medicos.controller;

import com.dan.commons.controller.CrudController;
import com.dan.commons.dto.medicos.MedicoRequest;
import com.dan.commons.dto.medicos.MedicoResponse;
import com.dan.msv.medicos.service.MedicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "API Médicos", description = "Métodos para la gestión de médicos")
public class MedicoController extends CrudController<MedicoRequest, MedicoResponse, MedicoService> {
    public MedicoController(MedicoService service) {
        super(service);
    }

    @GetMapping("/id-medico/{id}")
    @Operation(summary = "Obtener médico por id sin importar el estado del registro")
    public ResponseEntity<MedicoResponse> obtenerMedicoPorIdSinEstado(
            @PathVariable @Positive(message = "El ID debe ser positivo") Long id
    ) {
        return ResponseEntity.ok(service.obtenerMedicoPorIdSinEstado(id));
    }

    @PutMapping("/{idMedico}/disponibilidad/{idDisponibilidad}")
    @Operation(summary = "Actualizar disponibilidad de médico" +
    "(no es endpoint libre, debe gestionarlo el sistema de Citas)")
    public ResponseEntity<Void> actualizarDisponibilidadMedico(
            @PathVariable @Positive(message = "El idMedico debe ser positivo") Long idMedico,
            @PathVariable @Positive(message = "El idDisponibilidad debe ser positivo") Long idDisponibilidad
    ) {
        service.actualizarDisponibilidadMedico(idMedico, idDisponibilidad);
        return ResponseEntity.noContent().build();
    }
}
