package br.com.dio.barber_shop_api.controller.response;

import java.math.BigDecimal;
import java.util.UUID;

public record HaircutTypeResponse(
        UUID id,
        String name,
        BigDecimal price
) {}
