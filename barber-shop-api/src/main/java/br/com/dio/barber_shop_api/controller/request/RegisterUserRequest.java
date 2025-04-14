package br.com.dio.barber_shop_api.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @Email @NotBlank String email,
        @NotBlank @Size(min = 6) String password
) {}
