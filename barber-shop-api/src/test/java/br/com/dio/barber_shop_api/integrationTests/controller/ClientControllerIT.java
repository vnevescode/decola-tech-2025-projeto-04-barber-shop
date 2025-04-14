package br.com.dio.barber_shop_api.integrationTests.controller;


import br.com.dio.barber_shop_api.controller.request.ClientRequest;
import br.com.dio.barber_shop_api.entity.UserEntity;
import br.com.dio.barber_shop_api.repository.IClientRepository;
import br.com.dio.barber_shop_api.repository.IUserRepository;
import br.com.dio.barber_shop_api.security.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class ClientControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private IClientRepository clientRepository;
    @Autowired private IUserRepository userRepository;
    @Autowired private JwtService jwtService;
    @Autowired private ObjectMapper objectMapper;

    private String jwtToken;
    private UUID userId;

    @BeforeEach
    void setUp() {
        clientRepository.deleteAll();
        userRepository.deleteAll();

        UserEntity user = UserEntity.builder()
                .email("cliente@teste.com")
                .password("senha123")
                .role(UserEntity.Role.ROLE_USER)
                .build();
        user = userRepository.save(user);
        userId = user.getId();

        jwtToken = "Bearer " + jwtService.generateToken(user);
    }

    @Test
    void shouldCreateClientSuccessfully() throws Exception {
        ClientRequest request = new ClientRequest("Carlos Silva", "11999999999", userId);

        mockMvc.perform(post("/clients")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Carlos Silva"))
                .andExpect(jsonPath("$.phone").value("11999999999"));
    }

    @Test
    void shouldReturnClientByUserId() throws Exception {
        var client = clientRepository.save(
                br.com.dio.barber_shop_api.entity.ClientEntity.builder()
                        .name("Cliente Vinculado")
                        .phone("11988887777")
                        .user(userRepository.findById(userId).get())
                        .build()
        );

        mockMvc.perform(get("/clients/user/" + userId)
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(client.getId().toString()))
                .andExpect(jsonPath("$.name").value("Cliente Vinculado"));
    }

    @Test
    void shouldReturnAllClients() throws Exception {
        clientRepository.save(br.com.dio.barber_shop_api.entity.ClientEntity.builder()
                .name("Cliente A")
                .phone("11999999900")
                .user(userRepository.findById(userId).get())
                .build());

        mockMvc.perform(get("/clients")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
