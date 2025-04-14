package br.com.dio.barber_shop_api.service;

import br.com.dio.barber_shop_api.controller.request.RegisterUserRequest;
import br.com.dio.barber_shop_api.controller.response.UserResponse;

public interface UserService {
    UserResponse register(RegisterUserRequest request);
}