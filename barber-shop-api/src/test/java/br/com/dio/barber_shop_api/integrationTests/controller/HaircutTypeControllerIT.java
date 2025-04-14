package br.com.dio.barber_shop_api.integrationTests.controller;

import br.com.dio.barber_shop_api.controller.request.HaircutTypeRequest;
import br.com.dio.barber_shop_api.entity.HaircutTypeEntity;
import br.com.dio.barber_shop_api.entity.UserEntity;
import br.com.dio.barber_shop_api.repository.IHaircutTypeRepository;
import br.com.dio.barber_shop_api.repository.IScheduleRepository;
import br.com.dio.barber_shop_api.repository.IUserRepository;
import br.com.dio.barber_shop_api.security.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class HaircutTypeControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private IUserRepository userRepository;
    @Autowired private IHaircutTypeRepository haircutTypeRepository;
    @Autowired
    private IScheduleRepository scheduleRepository;
    @Autowired private JwtService jwtService;
    @Autowired private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeEach
    void setup() {
        scheduleRepository.deleteAll();
        haircutTypeRepository.deleteAll();
        userRepository.deleteAll();

        UserEntity admin = UserEntity.builder()
                .email("admin@teste.com")
                .password("123456")
                .role(UserEntity.Role.ROLE_ADMIN)
                .build();

        userRepository.save(admin);
        jwtToken = "Bearer " + jwtService.generateToken(admin);
    }

    @Test
    @DisplayName("✅ Deve criar tipo de corte com sucesso")
    void shouldCreateHaircutType() throws Exception {
        HaircutTypeRequest request = new HaircutTypeRequest("Corte Social", new BigDecimal("35.00"));

        mockMvc.perform(post("/haircuts")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Corte Social"))
                .andExpect(jsonPath("$.price").value(35.00));
    }

    @Test
    @DisplayName("📋 Deve retornar todos os tipos de corte")
    void shouldReturnAllHaircutTypes() throws Exception {
        haircutTypeRepository.save(HaircutTypeEntity.builder()
                .name("Degradê")
                .price(new BigDecimal("40.00"))
                .build());

        mockMvc.perform(get("/haircuts")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("📑 Deve retornar tipos de corte paginados")
    void shouldReturnPagedHaircutTypes() throws Exception {
        for (int i = 1; i <= 5; i++) {
            haircutTypeRepository.save(HaircutTypeEntity.builder()
                    .name("Corte " + i)
                    .price(BigDecimal.valueOf(30 + i))
                    .build());
        }

        mockMvc.perform(get("/haircuts/paged?page=0&size=3")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)));
    }

    @Test
    @DisplayName("🔍 Deve verificar existencia de tipo de corte por nome")
    void shouldCheckIfHaircutTypeExistsByName() throws Exception {
        haircutTypeRepository.save(HaircutTypeEntity.builder()
                .name("Navalhado")
                .price(new BigDecimal("45.00"))
                .build());

        mockMvc.perform(get("/haircuts/exists?name=Navalhado")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        mockMvc.perform(get("/haircuts/exists?name=Inexistente")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}
