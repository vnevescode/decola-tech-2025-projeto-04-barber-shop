package br.com.dio.barber_shop_api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequest (
        @Email
        @NotBlank String email,
        @NotBlank String password
) {}
