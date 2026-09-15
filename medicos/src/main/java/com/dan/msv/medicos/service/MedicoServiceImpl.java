package com.dan.msv.medicos.service;

import com.dan.commons.dto.medicos.MedicoRequest;
import com.dan.commons.dto.medicos.MedicoResponse;
import com.dan.commons.enums.DisponibilidadMedico;
import com.dan.commons.enums.EspecialidadMedico;
import com.dan.commons.enums.EstadoRegistro;
import com.dan.commons.exceptions.RecursoNoEncontradoException;
import com.dan.msv.medicos.entity.Medico;
import com.dan.msv.medicos.mapper.MedicoMapper;
import com.dan.msv.medicos.repository.MedicoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class MedicoServiceImpl implements MedicoService {
    private final MedicoRepository medicoRepository;
    private final MedicoMapper medicoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<MedicoResponse> listar() {
        log.info("Listando todos los médicos activos");

        return medicoRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(medicoMapper::entidadAResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MedicoResponse obtenerPorId(Long id) {
        return medicoMapper.entidadAResponse(obtenerMEdicoActivoPorId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public MedicoResponse obtenerMedicoPorIdSinEstado(Long id) {
        return medicoMapper.entidadAResponse(obtenerMEdicoPorIdSinEstado(id));
    }

    @Override
    public MedicoResponse registrar(MedicoRequest request) {
        log.info("Registrando nuevo médico: {}", request.nombre());

        Medico medico = medicoMapper.requestAEntidad(request);

        validarDatosUnicos(request);

        medico.actualizarEspecialidad(
                EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad()));

        medicoRepository.save(medico);

        log.info("Nuevo médico registrado: {}", medico.getNombre());

        return medicoMapper.entidadAResponse(medico);
    }


    @Override
    public MedicoResponse actualizar(MedicoRequest request, Long id) {
        Medico medico = obtenerMEdicoActivoPorId(id);

        log.info("Actualizando médico con id: {}", id);

        validarCambiosUnicos(request, id);

        medico.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.edad(),
                request.email(),
                request.telefono(),
                request.cedulaProfesional(),
                EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad()));

        log.info("Médico actualizado correctamente");

        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad) {
        Medico medico = obtenerMEdicoActivoPorId(idMedico);

        log.info("Actualizando disponibilidad del médico con id: {}", idMedico);

        DisponibilidadMedico nuevaDisponibilidad = DisponibilidadMedico.obtenerDisponibilidaPorCodigo(idDisponibilidad);

        DisponibilidadMedico disponibilidadAnterior = medico.getDisponibilidad();

        medico.actualizarDisponibilidad(nuevaDisponibilidad);

        log.info("Disponibilidad del médico con id {} cambió de {} a {}", idMedico, disponibilidadAnterior, nuevaDisponibilidad);
    }


    @Override
    public void eliminar(Long id) {
        Medico medico = obtenerMEdicoActivoPorId(id);

        log.info("Eliminando médico con id: {}", id);

        medico.eliminar();

        log.info("Médico eliminado exitósamente");
    }

    private Medico obtenerMEdicoActivoPorId(Long id) {
        log.info("Buscando médico con id {}", id);

        return medicoRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Médico activo no encontrado con id: " + id));
    }

    private Medico obtenerMEdicoPorIdSinEstado(Long id) {
        log.info("Buscando médico sin estado con id {}", id);

        return medicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Médico sin estado no encontrado con id: " + id));
    }

    private void validarDatosUnicos(MedicoRequest request) {
        log.info("Validando email único...");

        if(medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistro(
                request.email(), EstadoRegistro.ACTIVO))
            throw new IllegalArgumentException("Ya existe un médico activo con el email: "
                    + request.email());

        log.info("Validando teléfono único...");

        if(medicoRepository.existsByTelefonoAndEstadoRegistro(
                request.telefono(), EstadoRegistro.ACTIVO))
            throw new IllegalArgumentException("Ya existe un médico activo con el teléfono: "
                    + request.telefono());

        log.info("Validando cédula profesional única...");

        if(medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistro(
                request.cedulaProfesional(), EstadoRegistro.ACTIVO))
            throw new IllegalArgumentException("Ya existe un médico activo registrado con la cédula profersional: "
                    + request.cedulaProfesional());
    }

    private void validarCambiosUnicos(MedicoRequest request, Long id) {
        log.info("Validando cambio en email único...");

        if(medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(
                request.email(), EstadoRegistro.ACTIVO, id))
            throw new IllegalArgumentException("Ya existe un médico activo con el email: "
                    + request.email());

        log.info("Validando cambio en teléfono único...");

        if(medicoRepository.existsByTelefonoAndEstadoRegistroAndIdNot(
                request.telefono(), EstadoRegistro.ACTIVO, id))
            throw new IllegalArgumentException("Ya existe un médico activo con el teléfono: "
                    + request.telefono());

        log.info("Validando cambio en cédula profesional única...");

        if(medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistroAndIdNot(
                request.cedulaProfesional(), EstadoRegistro.ACTIVO, id))
            throw new IllegalArgumentException("Ya existe un médico activo registrado con la cédula profersional: "
                    + request.cedulaProfesional());
    }
}
