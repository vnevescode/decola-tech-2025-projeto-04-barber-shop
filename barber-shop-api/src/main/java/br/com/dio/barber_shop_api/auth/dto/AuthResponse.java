package br.com.dio.barber_shop_api.auth.dto;

import java.util.UUID;

public record AuthResponse (
        String token, UUID userId, boolean isClient
) {}
