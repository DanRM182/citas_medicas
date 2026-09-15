package com.dan.msv.medicos.repository;

import com.dan.commons.enums.EstadoRegistro;
import com.dan.msv.medicos.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
    List<Medico> findByEstadoRegistro(EstadoRegistro estadoRegistro);

    Optional<Medico> findByIdAndEstadoRegistro(Long id, EstadoRegistro estadoRegistro);

    boolean existsByEmailIgnoreCaseAndEstadoRegistro(String email, EstadoRegistro estadoRegistro);

    boolean existsByTelefonoAndEstadoRegistro(String telefono, EstadoRegistro estadoRegistro);

    boolean existsByCedulaProfesionalIgnoreCaseAndEstadoRegistro(String cedulaProfesional, EstadoRegistro estadoRegistro);

    boolean existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(String email, EstadoRegistro estadoRegistro, Long id);

    boolean existsByTelefonoAndEstadoRegistroAndIdNot(String telefono, EstadoRegistro estadoRegistro, Long id);

    boolean existsByCedulaProfesionalIgnoreCaseAndEstadoRegistroAndIdNot(String cedulaProfesional, EstadoRegistro estadoRegistro, Long id);
}
