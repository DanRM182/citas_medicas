package com.dan.auth.services;

import com.dan.auth.dto.LoginRequest;
import com.dan.auth.dto.TokenResponse;

public interface AuthService {

    TokenResponse autenticar(LoginRequest request) throws Exception;
}
