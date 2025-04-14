package br.com.dio.barber_shop_api.controller.response;

import br.com.dio.barber_shop_api.entity.UserEntity;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        UserEntity.Role role
) {}
