package com.dan.msv.citas.repository;

import com.dan.commons.enums.EstadoRegistro;
import com.dan.msv.citas.entity.Cita;
import com.dan.msv.citas.enums.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByEstadoRegistro(EstadoRegistro estadoRegistro);

    boolean existsByIdPacienteAndEstadoCitaIn(Long idPaciente, Collection<EstadoCita> estadoCitas);

    boolean existsByIdMedicoAndEstadoCitaIn(Long idMedico, Collection<EstadoCita> estadoCitas);

    boolean existsByIdAndEstadoCitaIn(Long id, Collection<EstadoCita> estadoCitas);
}
