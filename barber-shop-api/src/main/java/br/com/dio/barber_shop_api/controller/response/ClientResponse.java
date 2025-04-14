package br.com.dio.barber_shop_api.controller.response;

import java.util.UUID;

public record ClientResponse(
        UUID id,
        String name,
        String phone
) {}
