package br.com.dio.barber_shop_api.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ClientRequest(
        @NotBlank String name,
        @NotBlank @Size(min = 11, max = 11) String phone,
        @NotNull UUID userId
) {}
