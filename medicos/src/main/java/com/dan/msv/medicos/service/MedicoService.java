package com.dan.msv.medicos.service;

import com.dan.commons.dto.medicos.MedicoRequest;
import com.dan.commons.dto.medicos.MedicoResponse;
import com.dan.commons.service.CrudService;

public interface MedicoService extends CrudService<MedicoRequest, MedicoResponse> {
    MedicoResponse obtenerMedicoPorIdSinEstado(Long id);

    void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad);
}
