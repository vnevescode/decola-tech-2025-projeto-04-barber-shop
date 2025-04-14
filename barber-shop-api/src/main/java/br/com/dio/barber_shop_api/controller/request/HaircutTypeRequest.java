package br.com.dio.barber_shop_api.controller.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record HaircutTypeRequest(
        @NotBlank String name,
        @DecimalMin("0.01") @DecimalMax("10000.00") BigDecimal price
) {}
