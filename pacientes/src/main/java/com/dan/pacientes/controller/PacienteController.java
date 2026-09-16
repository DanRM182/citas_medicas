package com.dan.pacientes.controller;


import com.dan.commons.controller.CrudController;
import com.dan.commons.dto.pacientes.PacienteRequest;
import com.dan.commons.dto.pacientes.PacienteResponse;
import com.dan.pacientes.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "API Pacientes", description = "Métodos para gestión de pacientes")
public class PacienteController extends CrudController<PacienteRequest, PacienteResponse, PacienteService> {
    public PacienteController(PacienteService service) {
        super(service);
    }

    @GetMapping("/id-paciente/{id}")
    @Operation(summary = "Obtener paciente por ID sin importar el estado del registro")
    public ResponseEntity<PacienteResponse> obtenerPacientePorIdSinEstado(
            @PathVariable @Positive(message = "El ID debe ser positivo") Long id
    ) {
        return ResponseEntity.ok(service.obtenerPorIdEstadoGeneral(id));
    }
}