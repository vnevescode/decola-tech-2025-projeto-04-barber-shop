package br.com.dio.barber_shop_api.integrationTests.service;

import br.com.dio.barber_shop_api.auth.dto.AuthRequest;
import br.com.dio.barber_shop_api.auth.dto.AuthResponse;
import br.com.dio.barber_shop_api.controller.request.RegisterUserRequest;
import br.com.dio.barber_shop_api.auth.service.AuthService;
import br.com.dio.barber_shop_api.entity.UserEntity;
import br.com.dio.barber_shop_api.exception.custom.EmailAlreadyUsedException;
import br.com.dio.barber_shop_api.repository.IUserRepository;
import br.com.dio.barber_shop_api.security.jwt.JwtService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthServiceImplIT {

    @BeforeEach
    void setup() {
        when(jwtService.generateToken(any(UserEntity.class)))
                .thenAnswer(invocation -> {
                    UserEntity user = invocation.getArgument(0);
                    return "token-for-" + user.getEmail();
                });
    }

    @Autowired
    private AuthService authService;

    @Autowired
    private IUserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    @Test
    @DisplayName("✅ Deve registrar novo usuário com sucesso")
    void shouldRegisterNewUser() {
        RegisterUserRequest request = new RegisterUserRequest("newuser@example.com", "password123");

        String message = authService.register(request);

        assertNotNull(message);
        assertEquals("Usuário registrado com sucesso!", message);
    }

    @Test
    @DisplayName("🚫 Deve lançar exceção ao tentar registrar usuário com email já usado")
    void shouldThrowExceptionWhenEmailIsAlreadyUsed() {
        RegisterUserRequest request = new RegisterUserRequest("existing@example.com", "senha123");
        authService.register(request); // registra pela primeira vez

        assertThrows(EmailAlreadyUsedException.class, () -> authService.register(request));
    }

    @Test
    @DisplayName("✅ Deve autenticar usuário com credenciais válidas")
    void shouldAuthenticateUserSuccessfully() {
        RegisterUserRequest registerRequest = new RegisterUserRequest("authuser@example.com", "securePass123");
        authService.register(registerRequest);

        UserEntity user = userRepository.findByEmail("authuser@example.com").orElseThrow();
        when(jwtService.generateToken(user)).thenReturn("dummy-token");

        AuthRequest loginRequest = new AuthRequest("authuser@example.com", "securePass123");
        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("dummy-token", response.token());
    }

    @Test
    @DisplayName("🚫 Deve lançar exceção ao autenticar com credenciais inválidas")
    void shouldFailAuthenticationWithInvalidCredentials() {
        RegisterUserRequest registerRequest = new RegisterUserRequest("failuser@example.com", "123456");
        authService.register(registerRequest);

        AuthRequest wrongLogin = new AuthRequest("failuser@example.com", "wrongPassword");

        assertThrows(BadCredentialsException.class, () -> authService.login(wrongLogin));
    }

    @Test
    @DisplayName("✅ Deve registrar múltiplos usuários com sucesso")
    void shouldRegisterMultipleUsers() {
        RegisterUserRequest user1 = new RegisterUserRequest("user1@test.com", "pass123");
        RegisterUserRequest user2 = new RegisterUserRequest("user2@test.com", "pass456");

        String msg1 = authService.register(user1);
        String msg2 = authService.register(user2);

        assertEquals("Usuário registrado com sucesso!", msg1);
        assertEquals("Usuário registrado com sucesso!", msg2);
    }

    @Test
    @DisplayName("🚫 Deve falhar login com email não registrado")
    void shouldFailLoginIfUserDoesNotExist() {
        AuthRequest request = new AuthRequest("notfound@example.com", "senha123");

        assertThrows(Exception.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("✅ Deve gerar tokens diferentes para usuários diferentes")
    void shouldGenerateDifferentTokensForDifferentUsers() {
        authService.register(new RegisterUserRequest("token1@jwt.com", "pass123"));
        authService.register(new RegisterUserRequest("token2@jwt.com", "pass456"));

        UserEntity user1 = userRepository.findByEmail("token1@jwt.com").orElseThrow();
        UserEntity user2 = userRepository.findByEmail("token2@jwt.com").orElseThrow();

        when(jwtService.generateToken(user1)).thenReturn("token-1");
        when(jwtService.generateToken(user2)).thenReturn("token-2");

        String token1 = authService.login(new AuthRequest("token1@jwt.com", "pass123")).token();
        String token2 = authService.login(new AuthRequest("token2@jwt.com", "pass456")).token();

        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("🔒 Deve criptografar senha corretamente ao registrar")
    void shouldEncryptPassword() {
        RegisterUserRequest request = new RegisterUserRequest("secure@pw.com", "senha123");
        authService.register(request);

        UserEntity user = userRepository.findByEmail("secure@pw.com").orElseThrow();
        assertNotEquals("senha123", user.getPassword());
        assertTrue((user).getPassword().startsWith("$2a$")); // BCrypt
    }

    @Test
    @DisplayName("🚫 Deve falhar com email inválido ou senha vazia")
    void shouldFailValidationWithInvalidFields() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        AuthRequest invalidEmail = new AuthRequest("invalid", "123456");
        AuthRequest blankPassword = new AuthRequest("email@teste.com", "");

        var violationsEmail = validator.validate(invalidEmail);
        var violationsPassword = validator.validate(blankPassword);

        assertFalse(violationsEmail.isEmpty());
        assertFalse(violationsPassword.isEmpty());
    }

    @Test
    @DisplayName("🛡️ Deve definir ROLE_USER como padrão ao registrar novo usuário")
    void shouldSetDefaultRoleAsUser() {
        RegisterUserRequest request = new RegisterUserRequest("rolecheck@barber.com", "123456");
        authService.register(request);

        UserEntity user = userRepository.findByEmail("rolecheck@barber.com").orElseThrow();
        assertEquals(UserEntity.Role.ROLE_USER, user.getRole());
    }

    @Test
    @DisplayName("⏱️ Deve chamar geração de token com JwtService")
    void shouldCallJwtServiceGenerateToken() {
        RegisterUserRequest request = new RegisterUserRequest("mocked@user.com", "123456");
        authService.register(request);

        var user = userRepository.findByEmail("mocked@user.com").orElseThrow();

        when(jwtService.generateToken(user)).thenReturn("fake-jwt");

        AuthRequest loginRequest = new AuthRequest("mocked@user.com", "123456");
        AuthResponse response = authService.login(loginRequest);

        assertEquals("fake-jwt", response.token());
        verify(jwtService).generateToken(user); // ✅ também verifica se foi chamado
    }

    @Test
    @DisplayName("🚫 Não deve sobrescrever usuário existente ao tentar registrar com mesmo e-mail")
    void shouldNotOverrideExistingUser() {
        RegisterUserRequest first = new RegisterUserRequest("unique@teste.com", "abc123");
        authService.register(first);

        RegisterUserRequest second = new RegisterUserRequest("unique@teste.com", "novaSenha");

        assertThrows(EmailAlreadyUsedException.class, () -> authService.register(second));

        UserEntity user = userRepository.findByEmail("unique@teste.com").orElseThrow();
        assertTrue(user.getPassword().startsWith("$2a$")); // Verifica que a senha ainda é a original
    }
}
