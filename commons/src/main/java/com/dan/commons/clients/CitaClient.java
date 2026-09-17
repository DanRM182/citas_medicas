package com.dan.commons.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "citas")
public interface CitaClient {
    @GetMapping("/{idPaciente}/estadoCitaPaciente")
    void validarEstadoCitasPaciente(
            @PathVariable Long idPaciente);


    @GetMapping("/{idMedico}/estadoCitaMedico")
    void validarEstadoCitasMedico(
            @PathVariable Long idMedico);
}
