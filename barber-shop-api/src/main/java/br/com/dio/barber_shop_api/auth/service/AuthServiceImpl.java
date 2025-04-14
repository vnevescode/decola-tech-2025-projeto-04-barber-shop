package br.com.dio.barber_shop_api.auth.service;

import br.com.dio.barber_shop_api.auth.dto.AuthRequest;
import br.com.dio.barber_shop_api.auth.dto.AuthResponse;
import br.com.dio.barber_shop_api.controller.request.RegisterUserRequest;
import br.com.dio.barber_shop_api.controller.response.UserResponse;
import br.com.dio.barber_shop_api.entity.UserEntity;
import br.com.dio.barber_shop_api.exception.custom.EmailAlreadyUsedException;
import br.com.dio.barber_shop_api.repository.IClientRepository;
import br.com.dio.barber_shop_api.repository.IUserRepository;
import br.com.dio.barber_shop_api.security.jwt.JwtService;
import br.com.dio.barber_shop_api.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final IUserRepository userRepository;
    private final IClientRepository clientRepository;
    private final UserService userService;
    private final PasswordEncoder encoder;

    @Override
    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        boolean isClient =  clientRepository.existsByUser(user);

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getId(), isClient);
    }

    @Override
    public String register(RegisterUserRequest request) {
        log.info("Registering user via AuthService");

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyUsedException("E-mail já cadastrado");
        }

        UserEntity entity = UserEntity.builder()
                .email(request.email())
                .password(encoder.encode(request.password()))
                .role(UserEntity.Role.ROLE_USER)
                .build();

        userRepository.save(entity);
        log.info("User registered on Data Base with email: {}", entity.getEmail());

        return "Usuário registrado com sucesso!";
    }
}
