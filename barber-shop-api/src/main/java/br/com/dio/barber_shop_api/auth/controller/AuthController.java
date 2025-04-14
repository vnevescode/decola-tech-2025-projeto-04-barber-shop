package br.com.dio.barber_shop_api.auth.controller;

import br.com.dio.barber_shop_api.auth.controller.doc.AuthControllerDoc;
import br.com.dio.barber_shop_api.auth.dto.AuthRequest;
import br.com.dio.barber_shop_api.auth.dto.AuthResponse;
import br.com.dio.barber_shop_api.auth.service.AuthService;
import br.com.dio.barber_shop_api.controller.request.RegisterUserRequest;
import br.com.dio.barber_shop_api.controller.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDoc {

    private final AuthService authService;

    @Override
    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid AuthRequest request) {
        log.info("POST /auth/login | email={}", request.email());
        return authService.login(request);
    }

    @Override
    @PostMapping("/register")
    public Map<String, String> register(@RequestBody @Valid RegisterUserRequest request) {
        log.info("POST /auth/register | email={}", request.email());
        String message = authService.register(request);
        return Map.of("message", message);
    }
}
