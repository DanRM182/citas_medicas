package com.dan.msv.medicos.mapper;

import com.dan.commons.dto.medicos.MedicoRequest;
import com.dan.commons.dto.medicos.MedicoResponse;
import com.dan.commons.enums.DisponibilidadMedico;
import com.dan.commons.enums.EstadoRegistro;
import com.dan.commons.mapper.CommonMapper;
import com.dan.msv.medicos.entity.Medico;
import org.springframework.stereotype.Component;

@Component
public class MedicoMapper implements CommonMapper<MedicoRequest, MedicoResponse, Medico> {
    @Override
    public Medico requestAEntidad(MedicoRequest request) {
        return request != null ?
                Medico.builder()
                .nombre(request.nombre().trim())
                .apellidoPaterno(request.apellidoPaterno().trim())
                .apellidoMaterno(request.apellidoMaterno().trim())
                .edad(request.edad())
                .email(request.email().toLowerCase().trim())
                .telefono(request.telefono().trim())
                .cedulaProfesional(request.cedulaProfesional().trim())
                .disponibilidad(DisponibilidadMedico.DISPONIBLE)
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build() : null;
    }

    @Override
    public MedicoResponse entidadAResponse(Medico entidad) {
        return entidad != null ?
                new MedicoResponse(
                        entidad.getId(),
                        String.join(" ",
                                entidad.getNombre(),
                                entidad.getApellidoPaterno(),
                                entidad.getApellidoMaterno()),
                        entidad.getEdad(),
                        entidad.getEmail(),
                        entidad.getTelefono(),
                        entidad.getCedulaProfesional(),
                        entidad.getEspecialidad().getDescripcion(),
                        entidad.getDisponibilidad().getDescripcion(),
                        entidad.getDisponibilidad().getCodigo()) : null;
    }
}
