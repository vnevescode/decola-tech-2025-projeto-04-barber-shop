package br.com.dio.barber_shop_api.integrationTests.controller;

import br.com.dio.barber_shop_api.auth.dto.AuthRequest;
import br.com.dio.barber_shop_api.controller.request.RegisterUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve registrar um novo usuário com sucesso")
    void registerUser_Success() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest(
                "teste_" + System.currentTimeMillis() + "@email.com", // email único
                "senhaForte123"
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuário registrado com sucesso!"));
    }

    @Test
    @DisplayName("Deve retornar erro 409 se email já estiver em uso")
    void registerUser_EmailAlreadyUsed() throws Exception {
        String email = "duplicado_" + System.currentTimeMillis() + "@email.com";
        RegisterUserRequest request = new RegisterUserRequest(email, "senha123");

        // Primeiro registro
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Segundo registro com mesmo e-mail
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("E-mail já cadastrado"));
    }

    @Test
    @DisplayName("Deve fazer login com sucesso e retornar token JWT")
    void login_Success() throws Exception {
        String email = "login_" + System.currentTimeMillis() + "@email.com";
        String password = "SenhaValida123";

        // Registra o usuário
        RegisterUserRequest registerRequest = new RegisterUserRequest(email, password);
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // Tenta login
        AuthRequest loginRequest = new AuthRequest(email, password);
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @DisplayName("Deve retornar erro 401 ao tentar login com senha incorreta")
    void login_InvalidCredentials() throws Exception {
        String email = "naoexiste_" + System.currentTimeMillis() + "@email.com";
        AuthRequest loginRequest = new AuthRequest(email, "senhaErrada");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}
