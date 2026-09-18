package com.dan.msv.citas.service;

import com.dan.commons.clients.MedicoClient;
import com.dan.commons.clients.PacienteClient;
import com.dan.commons.dto.medicos.MedicoResponse;
import com.dan.commons.dto.pacientes.PacienteResponse;
import com.dan.commons.enums.DisponibilidadMedico;
import com.dan.commons.enums.EstadoRegistro;
import com.dan.commons.exceptions.EntidadRelacionadaException;
import com.dan.commons.exceptions.RecursoNoEncontradoException;
import com.dan.msv.citas.dto.CitaRequest;
import com.dan.msv.citas.dto.CitaResponse;
import com.dan.msv.citas.entity.Cita;
import com.dan.msv.citas.enums.EstadoCita;
import com.dan.msv.citas.mapper.CitaMapper;
import com.dan.msv.citas.repository.CitaRepository;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class CitaServiceImpl implements CitaService {
    private final CitaRepository citaRepository;
    private final CitaMapper citaMapper;
    private final MedicoClient medicoClient;
    private final PacienteClient pacienteClient;

    private final List<EstadoCita> VALIDAR_ESTADOS_PACIENTE =
            List.of(EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA,
                    EstadoCita.EN_CURSO);
    private final List<EstadoCita> VALIDAR_CONFIRMADA_EN_CURSO =
            List.of(EstadoCita.CONFIRMADA, EstadoCita.EN_CURSO);

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

        validarCitaActiva(request.idPaciente(), VALIDAR_ESTADOS_PACIENTE,
                "No se puede registrar porque el paciente tiene una cita activa",
                citaRepository::existsByIdPacienteAndEstadoCitaIn);

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
        Long idMedicoAnterior = null;

        log.info("Actualizando cita con id: {}", id);

        Cita cita = obtenerCitaOException(id);

        if(!cita.getEstadoCita().isActualizable())
        throw new EntidadRelacionadaException("La cita con ID: " + id + " no se puede actualizar" +
                " porque no está en estado PENDIENTE o CONFIRMADA");

        idMedicoAnterior = cita.getIdMedico();

        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());

        PacienteResponse paciente = obtenerPacienteActivo(request.idPaciente());

        if(!request.idMedico().equals(idMedicoAnterior))
            validarMedicoActivoDisponible(medico);

        if(!request.idPaciente().equals(cita.getIdPaciente()))
            validarCitaActiva(request.idPaciente(), VALIDAR_ESTADOS_PACIENTE,
                    "El paciente tiene cita con estado PENDIENTE o CONFIRMADA o EN_CURSO",
                    citaRepository::existsByIdPacienteAndEstadoCitaIn);

        cita.actualizar(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas());

        citaRepository.save(cita);

        if(!request.idMedico().equals(idMedicoAnterior)) {
            actualizarDisponibilidadMedico(idMedicoAnterior,
                    DisponibilidadMedico.DISPONIBLE.getCodigo());

            cambiarDisponibilidadMedicoSegunEstadoCita(medico.id(),
                    cita.getEstadoCita());
        }

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
    public void validarEstadoCitasDePaciente(Long idPaciente) {
        log.info("Validando si el paciente con id {} tiene citas en estado CONFIRMADA o EN_CURSO",
                idPaciente);

        validarCitaActiva(idPaciente, VALIDAR_CONFIRMADA_EN_CURSO,
                "El paciente tiene citas en estado CONFIRMADA o EN_CURSO",
                citaRepository::existsByIdPacienteAndEstadoCitaIn);
    }

    @Override
    public void validarEstadoCitasDeMedico(Long idMedico) {
        log.info("Validando si el médico con id {} tiene citas en estado CONFIRMADA o EN_CURSO",
                idMedico);

        validarCitaActiva(idMedico, VALIDAR_CONFIRMADA_EN_CURSO,
                "El Médico tiene cita con estado CONFIRMADA o EN_CURSO",
                citaRepository::existsByIdMedicoAndEstadoCitaIn);
    }

    @Override
    public void eliminar(Long id) {
        Cita cita = obtenerCitaOException(id);

        log.info("Eliminando cita con id: {}", id);

        if(!cita.getEstadoCita().isEliminable())
            throw new EntidadRelacionadaException("La cita con ID: " + id + " no se puede eliminar" +
                    " porque no está en estado PENDIENTE o CANCELADA o FINALIZADA");

        cita.eliminar();

        citaRepository.save(cita);

        if(cita.getEstadoCita() == EstadoCita.PENDIENTE)
            actualizarDisponibilidadMedico(cita.getIdMedico(), DisponibilidadMedico.DISPONIBLE.getCodigo());

        log.info("Cita con id {} ha sido marcada como eliminada", id);
    }


    private Cita obtenerCitaOException(Long id) {
        log.info("Buscando cita con id: {}", id);

        return citaRepository.findById(id).orElseThrow(() ->
                new RecursoNoEncontradoException("Cita no encontrada con id: " + id));
    }

    private MedicoResponse obtenerMedicoActivo(Long id) {
        log.info("Buscando médico activo con id {} en el servicio remoto...", id);

        return validarObjetoRecibido(id,medicoClient::obtenerMedicoActivoPorId,
                "Medico activo no encontrado con id: " + id);
    }

    private MedicoResponse obtenerMedicoSinEstado(Long id) {
        log.info("Buscando médico sin estado con id {} en el servicio remoto...", id);

        return validarObjetoRecibido(id,medicoClient::obtenerMedicoSinEstadoPorId,
                "Medico no encontrado con id: " + id);
    }

    private PacienteResponse obtenerPacienteActivo(Long id) {
        log.info("Buscando paciente activo con id {} en el servicio remoto...", id);

        return validarObjetoRecibido(id,pacienteClient::obtenerPacienteActivoPorId,
                "Paciente activo no encontrado con id: " + id);
    }

    private PacienteResponse obtenerPacienteSinEstado(Long id) {
        log.info("Buscando paciente sin estado con id {} en el servicio remoto...", id);

        return validarObjetoRecibido(id,pacienteClient::obtenerPacienteSinEstadoPorId,
                "Paciente no encontrado con id: " + id);
    }

    private  void validarMedicoActivoDisponible(MedicoResponse medico) {
        log.info("Validando si el medico activo esta disponible");

        if (!DisponibilidadMedico.DISPONIBLE.getCodigo().equals(medico.idDisponibilidad()))
            throw  new IllegalStateException("El medico no esta disponible para consulta");
    }

    private void validarCitaActiva(Long id, List<EstadoCita> estadosCita, String mensaje,
                                   BiFunction<Long, List<EstadoCita>, Boolean> existeCita) {
        log.info("Validando si existe cita activa...");

        if(existeCita.apply(id, estadosCita))
            throw  new EntidadRelacionadaException(mensaje);
    }

    private <T, R> R validarObjetoRecibido(T objeto, Function<T, R> obtenerObjeto, String mensaje) {
        try {
            return obtenerObjeto.apply(objeto);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException(mensaje);
        }
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
}
