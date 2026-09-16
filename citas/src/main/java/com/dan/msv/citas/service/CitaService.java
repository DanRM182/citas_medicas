package com.dan.msv.citas.service;

import com.dan.commons.service.CrudService;
import com.dan.msv.citas.dto.CitaRequest;
import com.dan.msv.citas.dto.CitaResponse;

public interface CitaService extends CrudService<CitaRequest, CitaResponse> {
    void actualizarEstadoCita(Long idCita, Long idEstadoCita);
}