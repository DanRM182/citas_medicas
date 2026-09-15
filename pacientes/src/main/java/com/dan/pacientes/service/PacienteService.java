package com.dan.pacientes.service;

import com.dan.commons.dto.pacientes.PacienteRequest;
import com.dan.commons.dto.pacientes.PacienteResponse;
import com.dan.commons.service.CrudService;

public interface PacienteService extends CrudService<PacienteRequest, PacienteResponse> {
    PacienteResponse obtenerPorIdEstadoGeneral(Long id);
}