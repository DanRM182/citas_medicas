package com.dan.pacientes.service;

import com.dan.commons.clients.CitaClient;
import com.dan.commons.dto.pacientes.PacienteRequest;
import com.dan.commons.dto.pacientes.PacienteResponse;
import com.dan.commons.exceptions.EntidadRelacionadaException;
import com.dan.commons.utils.FunctionUtils;
import com.dan.pacientes.entity.Paciente;
import com.dan.commons.enums.EstadoRegistro;
import com.dan.commons.exceptions.RecursoNoEncontradoException;
import com.dan.pacientes.mapper.PacienteMapper;
import com.dan.pacientes.repository.PacienteRepository;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class PacienteServiceImpl implements PacienteService {
    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;
    private final CitaClient citaClient;

    @Override
    @Transactional(readOnly = true)
    public List<PacienteResponse> listar() {
        log.info("Listando todos los pacientes activos");

        return pacienteRepository.findAllByEstadoRegistro(EstadoRegistro.ACTIVO)
                .stream().map(pacienteMapper::entidadAResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteResponse obtenerPorId(Long id) {
        log.info("Buscando paciente activo por ID");

        return pacienteMapper.entidadAResponse(obtenerPacienteActivo(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteResponse obtenerPorIdEstadoGeneral(Long id) {
        log.info("Buscando paciente por ID");

        return pacienteMapper.entidadAResponse(obtenerPaciente(id));
    }

    @Override
    public PacienteResponse registrar(PacienteRequest request) {
        log.info("Registrando nuevo paciente...");

        Paciente paciente = pacienteMapper.requestAEntidad(request, generarImc(request), generarNumeroExpediente(request), EstadoRegistro.ACTIVO);

        validarUnicidad(request);

        pacienteRepository.save(paciente);

        log.info("Paciente registrado y estado activo con ID: {}", paciente.getId());

        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override
    public PacienteResponse actualizar(PacienteRequest request, Long id) {
        Paciente paciente = obtenerPacienteActivo(id);

        log.info("Actualizando paciente con ID: {}", id);

        validarUnicidadCambios(request, id);

        validarEstadoCitasPaciente(id);

        paciente.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                paciente.getApellidoMaterno(),
                paciente.getEdad(),
                paciente.getPeso(),
                paciente.getEstatura(),
                generarImc(request),
                paciente.getEmail(),
                generarNumeroExpediente(request),
                paciente.getTelefono(),
                paciente.getDireccion(),
                EstadoRegistro.ACTIVO);

        log.info("Paciente con ID {} actualizado correctamente", id);

        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override
    public void eliminar(Long id) {
        Paciente paciente = obtenerPaciente(id);

        log.info("Eliminando paciente con ID: {}", id);

        validarEstadoCitasPaciente(id);

        paciente.eliminar();

        log.info("Paciente con ID {} eliminado correctamente.", id);
    }

    private Paciente obtenerPacienteActivo(Long id) {
        log.info("Buscando paciente activo con ID {}", id);

        return pacienteRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Paciente activo con ID: " + id + " no encontrado."));
    }

    private Paciente obtenerPaciente(Long id) {
        log.info("Buscando paciente con ID {}", id);

        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Paciente con ID: " + id + " no encontrado."));
    }

    private void validarUnicidad(PacienteRequest request) {
        log.info("Validando unicidad de email y teléfono del paciente activo");

        if(pacienteRepository.existsByEmailIgnoreCaseAndEstadoRegistro(request.email(), EstadoRegistro.ACTIVO))
            throw new IllegalArgumentException("El email ingresado ya está registrado en un usuario " +
                    "con estado Activo");

        if(pacienteRepository.existsByTelefonoAndEstadoRegistro(request.email(), EstadoRegistro.ACTIVO))
            throw new IllegalArgumentException("El teléfono ingresado ya está registrado en un usuario " +
                    "con estado Activo");
    }

    private void validarUnicidadCambios(PacienteRequest request, Long id) {
        log.info("Validando unicidad de email y teléfono del paciente activo");

        if(pacienteRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(request.email(),
                EstadoRegistro.ACTIVO, id))
            throw new IllegalArgumentException("El email " + request.email() + " ya está registrado" +
                    " en un paciente con estado Activo");

        if(pacienteRepository.existsByTelefonoAndEstadoRegistroAndIdNot(request.email(),
                EstadoRegistro.ACTIVO, id))
            throw new IllegalArgumentException("El teléfono " + request.telefono() + " ya está " +
                    "registrado en un paciente con estado Activo");
    }

    private Double generarImc(PacienteRequest request) {
        log.info("Generando IMC..");

        return pacienteRepository.generarImc(request.peso(), request.estatura());
    }

    private String generarNumeroExpediente(PacienteRequest request) {
        log.info("Generando Número de Registro");

        return pacienteRepository.generarExpediente(request.telefono());
    }

    private void validarEstadoCitasPaciente(Long id) {
        log.info("Validando si el paciente tiene citas con estado CONFIRMADA o EN_CURSO");

        FunctionUtils.validarEstadoCitas(id, citaClient::validarEstadoCitasPaciente,
                "El paciente con ID: " +id + " tiene actualmente una cita activa");
    }
}
