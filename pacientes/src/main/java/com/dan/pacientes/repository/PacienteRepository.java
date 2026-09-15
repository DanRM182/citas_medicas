package com.dan.pacientes.repository;

import com.dan.pacientes.entity.Paciente;
import com.dan.commons.enums.EstadoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    @Query(nativeQuery = true, value = "SELECT GENERAR_IMC(:peso, :estatura)")
    Double generarImc(@Param("peso") Double peso,
                          @Param("estatura") Double estatura);

    @Query(nativeQuery = true, value = "SELECT GENERAR_EXPEDIENTE(:telefono)")
    String generarExpediente(@Param("telefono") String telefono);

    Optional<Paciente> findByIdAndEstadoRegistro(Long id, EstadoRegistro estadoRegistro);

    List<Paciente> findAllByEstadoRegistro(EstadoRegistro estadoRegistro);

    boolean existsByEmailIgnoreCaseAndEstadoRegistro(String email, EstadoRegistro estadoRegistro);

    boolean existsByTelefonoAndEstadoRegistro(String numero, EstadoRegistro estadoRegistro);
}
