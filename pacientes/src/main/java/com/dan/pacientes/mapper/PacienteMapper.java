package com.dan.pacientes.mapper;

import com.dan.commons.dto.pacientes.PacienteRequest;
import com.dan.commons.dto.pacientes.PacienteResponse;
import com.dan.commons.mapper.CommonMapper;
import com.dan.pacientes.entity.Paciente;
import com.dan.commons.enums.EstadoRegistro;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PacienteMapper implements CommonMapper<PacienteRequest, PacienteResponse, Paciente> {
    @Override
    public Paciente requestAEntidad(PacienteRequest request) {
        return request != null ?
                Paciente.builder()
                        .nombre(request.nombre().trim())
                        .apellidoPaterno(request.apellidoPaterno().trim())
                        .apellidoMaterno(request.apellidoMaterno().trim())
                        .edad(request.edad())
                        .peso(request.peso())
                        .estatura(request.estatura())
                        .email(request.email().trim())
                        .telefono(request.telefono().trim())
                        .direccion(request.direccion().trim())
                        .build() : null;
    }

    public Paciente requestAEntidad(PacienteRequest request, Double imc, String numExpediente, EstadoRegistro estadoRegistro) {
        if(request == null) return null;

        Paciente paciente = requestAEntidad(request);

        paciente.asignarImcNumExpEstadoReg(imc, numExpediente, estadoRegistro);

        return paciente;
    }

    @Override
    public PacienteResponse entidadAResponse(Paciente entidad) {
        if(entidad == null || entidad.getEstadoRegistro() == null)
            return null;

        return new PacienteResponse(
                entidad.getId(),
                entidad.obtenerNombreCompleto(
                        entidad.getNombre(),
                        entidad.getApellidoPaterno(),
                        entidad.getApellidoMaterno()),
                entidad.getEdad(),
                entidad.getPeso(),
                entidad.getEstatura(),
                entidad.getImc(),
                entidad.getEmail(),
                entidad.getNumExpediente(),
                entidad.getTelefono(),
                entidad.getDireccion());
    }

}
