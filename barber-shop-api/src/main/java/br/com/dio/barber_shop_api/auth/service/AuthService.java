package br.com.dio.barber_shop_api.auth.service;

import br.com.dio.barber_shop_api.auth.dto.AuthRequest;
import br.com.dio.barber_shop_api.auth.dto.AuthResponse;
import br.com.dio.barber_shop_api.controller.request.RegisterUserRequest;
import br.com.dio.barber_shop_api.controller.response.UserResponse;

public interface AuthService {
    AuthResponse login(AuthRequest request);
    String register(RegisterUserRequest request);
}
