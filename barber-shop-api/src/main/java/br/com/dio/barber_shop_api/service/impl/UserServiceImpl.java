package br.com.dio.barber_shop_api.service.impl;

import br.com.dio.barber_shop_api.controller.request.RegisterUserRequest;
import br.com.dio.barber_shop_api.controller.response.UserResponse;
import br.com.dio.barber_shop_api.entity.UserEntity;
import br.com.dio.barber_shop_api.exception.custom.EmailAlreadyUsedException;
import br.com.dio.barber_shop_api.mapper.UserMapper;
import br.com.dio.barber_shop_api.repository.IUserRepository;
import br.com.dio.barber_shop_api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final IUserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder encoder;

    @Override
    public UserResponse register(RegisterUserRequest request) {
        log.info("Attempting to register user with email: {}", request.email());

        if (repository.existsByEmail(request.email())) {
            log.warn("Email already registered: {}", request.email());
            throw new EmailAlreadyUsedException("E-mail já cadastrado");
        }

        UserEntity entity = UserEntity.builder()
                .email(request.email())
                .password(encoder.encode(request.password()))
                .role(UserEntity.Role.ROLE_USER)
                .build();

        UserResponse response = mapper.toResponse(repository.save(entity));
        log.info("User registered with ID: {}", response.id());
        return response;
    }
}