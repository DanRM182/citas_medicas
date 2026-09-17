package com.dan.msv.citas.mapper;

import com.dan.commons.dto.medicos.DatosMedico;
import com.dan.commons.dto.medicos.MedicoResponse;
import com.dan.commons.dto.pacientes.DatosPaciente;
import com.dan.commons.dto.pacientes.PacienteResponse;
import com.dan.commons.mapper.CommonMapper;
import com.dan.msv.citas.dto.CitaRequest;
import com.dan.msv.citas.dto.CitaResponse;
import com.dan.msv.citas.entity.Cita;
import org.springframework.stereotype.Component;

@Component
public class CitaMapper implements CommonMapper<CitaRequest, CitaResponse, Cita> {
    @Override
    public Cita requestAEntidad(CitaRequest request) {
        return request != null ?
                Cita.crear(
                        request.idPaciente(),
                        request.idMedico(),
                        request.fechaCita(),
                        request.sintomas().trim()) : null;
    }

    @Override
    public CitaResponse entidadAResponse(Cita entidad) {
        return entidad != null ?
                new CitaResponse(
                        entidad.getId(),
                        null, null,
                        entidad.getFechaCita(),
                        entidad.getSintomas(),
                        entidad.getEstadoCita().getDescripcion()) : null;
    }

    public CitaResponse entidadAResponse(Cita entidad, PacienteResponse paciente, MedicoResponse medico) {
        return entidad != null ?
                new CitaResponse(
                        entidad.getId(),
                        pacienteResponseADatosPaciente(paciente),
                        medicoResponseADatosMedico(medico),
                        entidad.getFechaCita(),
                        entidad.getSintomas(),
                        entidad.getEstadoCita().getDescripcion()) : null;
    }

    private DatosPaciente pacienteResponseADatosPaciente(PacienteResponse paciente) {
        return paciente != null ?
                new DatosPaciente(
                        paciente.nombre(),
                        paciente.numExpediente(),
                        paciente.edad() + " años",
                        paciente.peso() + " kg.",
                        paciente.estatura() + " m.",
                        String.join(" ",
                                Math.round(paciente.imc() * 100.0) /100.0 + "",
                                clasificacionIMC(paciente.imc())),
                        paciente.telefono()) : null;
    }

    private String clasificacionIMC(double imc) {
        if(imc < 18.5) return "Bajo peso";
        if(imc < 25) return "Peso normal";
        if(imc < 30) return "Sobrepeso";
        if(imc < 35) return "Obesidad grado I";
        if(imc < 40) return "Obesidad grado II";
        return "Obesidad grado III";
    }

    private DatosMedico medicoResponseADatosMedico(MedicoResponse medico) {
        return medico != null ?
                new DatosMedico(
                        medico.nombre(),
                        medico.cedulaProfesional(),
                        medico.especialidad()) : null;
    }
}
