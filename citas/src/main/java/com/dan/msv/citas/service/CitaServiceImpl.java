package com.dan.msv.citas.service;

import com.dan.commons.clients.MedicoClient;
import com.dan.commons.clients.PacienteClient;
import com.dan.commons.dto.medicos.MedicoResponse;
import com.dan.commons.dto.pacientes.PacienteResponse;
import com.dan.commons.enums.DisponibilidadMedico;
import com.dan.commons.enums.EstadoRegistro;
import com.dan.commons.exceptions.RecursoNoEncontradoException;
import com.dan.commons.utils.FeignUtils;
import com.dan.msv.citas.dto.CitaRequest;
import com.dan.msv.citas.dto.CitaResponse;
import com.dan.msv.citas.entity.Cita;
import com.dan.msv.citas.enums.EstadoCita;
import com.dan.msv.citas.mapper.CitaMapper;
import com.dan.msv.citas.repository.CitaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class CitaServiceImpl implements CitaService {
    private final CitaRepository citaRepository;
    private final CitaMapper citaMapper;
    private final MedicoClient medicoClient;
    private final PacienteClient pacienteClient;

    @Override
    public List<CitaResponse> listar() {
        log.info("Listando todas las citas activas");

        return citaRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(cita -> citaMapper.entidadAResponse(
                        cita,
                        obtenerPacienteSinEstado(cita.getIdPaciente()),
                        obtenerMedicoSinEstado(cita.getIdMedico())
                )).toList();
    }

    @Override
    public CitaResponse obtenerPorId(Long id) {
        Cita cita = obtenerCitaOException(id);

        return citaMapper.entidadAResponse(
                cita,
                obtenerPacienteSinEstado(cita.getIdPaciente()),
                obtenerMedicoSinEstado(cita.getIdMedico())
        );
    }

    @Override
    public CitaResponse registrar(CitaRequest request) {
        log.info("Registrando nueva cita...");

        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());

        PacienteResponse paciente = obtenerPacienteActivo(request.idPaciente());

        validarMedicoActivoDisponible(medico);

        validarPacienteCitaActiva(paciente);

        Cita cita = citaMapper.requestAEntidad(request);

        citaRepository.save(cita);

        cambiarDisponibilidadMedicoSegunEstadoCita(medico.id(), cita.getEstadoCita());

        log.info("Cita registrada exitósamente");

        return citaMapper.entidadAResponse(
                cita,
                paciente,
                medico
        );
    }

    @Override
    public CitaResponse actualizar(CitaRequest request, Long id) {
        validarCitaActiva(id);

        log.info("Actualizando cita con id: {}", id);

        Cita cita = obtenerCitaOException(id);

        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());

        PacienteResponse paciente = obtenerPacienteActivo(request.idPaciente());

        if(!request.idMedico().equals(cita.getIdMedico())) {
            validarMedicoActivoDisponible(medico);

            actualizarDisponibilidadMedico(cita.getIdMedico(),
                    DisponibilidadMedico.DISPONIBLE.getCodigo());

            cambiarDisponibilidadMedicoSegunEstadoCita(medico.id(),
                    cita.getEstadoCita());
        }

        if(!request.idPaciente().equals(cita.getIdPaciente()))
            validarPacienteCitaActiva(paciente);

        cita.actualizar(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas());

        log.info("Cita actualizada con id: {}", id);

        return citaMapper.entidadAResponse(
                cita,
                paciente,
                medico
        );
    }

    @Override
    public void actualizarEstadoCita(Long idCita, Long idEstadoCita) {
        Cita cita = obtenerCitaOException(idCita);

        log.info("Actualizando estado de la cita con id: {}", idCita);

        cita.actualizarEstadoCita(EstadoCita.obtenerEstadoCitaPorCodigo(idEstadoCita));

        citaRepository.save(cita);

        cambiarDisponibilidadMedicoSegunEstadoCita(cita.getIdMedico(), cita.getEstadoCita());

        log.info("Estado de la cita {} actualizado correctamente", idCita);
    }

    @Override
    public void eliminar(Long id) {
        Cita cita = obtenerCitaOException(id);

        log.info("Eliminando cita con id: {}", id);

        cita.eliminar();

        if(cita.getEstadoCita() == EstadoCita.PENDIENTE)
            actualizarDisponibilidadMedico(cita.getIdMedico(), DisponibilidadMedico.DISPONIBLE.getCodigo());

        cambiarDisponibilidadMedicoSegunEstadoCita(cita.getIdMedico(), cita.getEstadoCita());

        log.info("Cita con id {} ha sido marcada como eliminada", id);
    }


    private Cita obtenerCitaOException(Long id) {
        log.info("Buscando cita con id: {}", id);

        return citaRepository.findById(id).orElseThrow(() ->
                new RecursoNoEncontradoException("Cita no encontrada con id: " + id));
    }

    private MedicoResponse obtenerMedicoActivo(Long id) {
        log.info("Buscando médico activo con id {} en el servicio remoto...", id);

        return FeignUtils.validarObjetoRecibido(id,medicoClient::obtenerMedicoActivoPorId,
                "Medico activo no encontrado con id: " + id);
    }

    private MedicoResponse obtenerMedicoSinEstado(Long id) {
        log.info("Buscando médico sin estado con id {} en el servicio remoto...", id);

        return FeignUtils.validarObjetoRecibido(id,medicoClient::obtenerMedicoSinEstadoPorId,
                "Medico no encontrado con id: " + id);
    }

    private PacienteResponse obtenerPacienteActivo(Long id) {
        log.info("Buscando paciente activo con id {} en el servicio remoto...", id);

        return FeignUtils.validarObjetoRecibido(id,pacienteClient::obtenerPacienteActivoPorId,
                "Paciente activo no encontrado con id: " + id);
    }

    private PacienteResponse obtenerPacienteSinEstado(Long id) {
        log.info("Buscando paciente sin estado con id {} en el servicio remoto...", id);

        return FeignUtils.validarObjetoRecibido(id,pacienteClient::obtenerPacienteSinEstadoPorId,
                "Paciente no encontrado con id: " + id);
    }

    private  void validarMedicoActivoDisponible(MedicoResponse medico) {
        log.info("Validando si el medico activo esta disponible");

        if (!DisponibilidadMedico.DISPONIBLE.getCodigo().equals(medico.idDisponibilidad()))
            throw  new IllegalStateException("El medico no esta disponible para consulta");
    }

    private void validarPacienteCitaActiva(PacienteResponse paciente) {
        log.info("Validando que el paciente no tenga citas activas...");

        if(citaRepository.existsByIdPacienteAndEstadoCitaIn(paciente.id(),
                List.of(EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA, EstadoCita.EN_CURSO)))
            throw  new IllegalStateException("El paciente tiene actualmente una cita activa");
    }

    private void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad) {
        log.info("Actualizando disponibilidad del médico en el servicio remoto...");

        medicoClient.actualizarDisponibilidadMedico(idMedico, idDisponibilidad);

        log.info("Disponibilidad del médico en el servicio remoto");
    }

    private void cambiarDisponibilidadMedicoSegunEstadoCita(Long idMedico, EstadoCita estadoCita) {
        switch (estadoCita) {
            case PENDIENTE, CONFIRMADA -> actualizarDisponibilidadMedico(idMedico,
                    DisponibilidadMedico.NO_DISPONIBLE.getCodigo());

            case EN_CURSO -> actualizarDisponibilidadMedico(idMedico,
                    DisponibilidadMedico.EN_CONSULTA.getCodigo());

            case FINALIZADA, CANCELADA -> actualizarDisponibilidadMedico(idMedico,
                    DisponibilidadMedico.DISPONIBLE.getCodigo());
        }
    }

    private void validarCitaActiva(Long id) {
        log.info("Validando estado de cita con id: {}", id);

        if(!citaRepository.existsByIdAndEstadoCitaIn(id, List.of(EstadoCita.PENDIENTE,
                EstadoCita.CONFIRMADA)))
            throw new IllegalStateException("La cita no puede actualizarse porque no " +
                    "tiene estado PENDIENTE o CONFIRMADA");

    }

}
