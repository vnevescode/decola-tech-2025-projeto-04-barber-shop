package br.com.dio.barber_shop_api.controller;

import br.com.dio.barber_shop_api.controller.request.RegisterUserRequest;
import br.com.dio.barber_shop_api.controller.response.UserResponse;
import br.com.dio.barber_shop_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public UserResponse register(@RequestBody @Valid RegisterUserRequest request) {
        log.info("POST /users/register | email: {}", request.email());
        return userService.register(request);
    }
}
