package br.com.dio.barber_shop_api.unitTests.service;

import br.com.dio.barber_shop_api.auth.dto.AuthRequest;
import br.com.dio.barber_shop_api.auth.dto.AuthResponse;
import br.com.dio.barber_shop_api.auth.service.AuthServiceImpl;
import br.com.dio.barber_shop_api.controller.request.RegisterUserRequest;
import br.com.dio.barber_shop_api.entity.UserEntity;
import br.com.dio.barber_shop_api.exception.custom.EmailAlreadyUsedException;
import br.com.dio.barber_shop_api.repository.IClientRepository;
import br.com.dio.barber_shop_api.repository.IUserRepository;
import br.com.dio.barber_shop_api.security.jwt.JwtService;
import br.com.dio.barber_shop_api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class AuthServiceImplTest {
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;
    @Mock private IUserRepository userRepository;
    @Mock private IClientRepository clientRepository;
    @Mock private UserService userService;
    @Mock private PasswordEncoder encoder;

    @InjectMocks private AuthServiceImpl authService;

    private final UUID userId = UUID.randomUUID();
    private final String email = "user@example.com";
    private final String password = "securePass123";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Deve autenticar o usuário com sucesso e retornar um token JWT
     */
    @Test
    void login_WithValidCredentials_ReturnsAuthResponse() {
        // Arrange
        AuthRequest request = new AuthRequest(email, password);
        UserEntity user = UserEntity.builder().id(userId).email(email).password("encoded").role(UserEntity.Role.ROLE_USER).build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("mocked-jwt");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("mocked-jwt", response.token());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(user);
    }

    /**
     * Deve lançar EntityNotFoundException caso o usuário não exista no login
     */
    @Test
    void login_WithInvalidEmail_ThrowsException() {
        AuthRequest request = new AuthRequest(email, password);

        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> authService.login(request));
    }

    /**
     * Deve registrar novo usuário quando email não estiver em uso
     */
    @Test
    void register_WithNewEmail_ReturnsSuccessMessage() {
        RegisterUserRequest request = new RegisterUserRequest(email, password);
        UserEntity user = UserEntity.builder()
                .email(email)
                .password("encodedPassword")
                .role(UserEntity.Role.ROLE_USER)
                .build();

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(encoder.encode(password)).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);

        String message = authService.register(request);

        assertEquals("Usuário registrado com sucesso!", message);
        verify(userRepository).save(any(UserEntity.class));
    }

    /**
     * Deve lançar exceção ao tentar registrar um email já existente
     */
    @Test
    void register_WithExistingEmail_ThrowsException() {
        RegisterUserRequest request = new RegisterUserRequest(email, password);
        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(EmailAlreadyUsedException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }
}
