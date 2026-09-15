package com.dan.auth.services;

import java.util.Set;

import com.dan.auth.dto.UsuarioRequest;
import com.dan.auth.dto.UsuarioResponse;

public interface UsuarioService {

    Set<UsuarioResponse> listar();

    UsuarioResponse registrar(UsuarioRequest request);

    UsuarioResponse eliminar(String username);
}
