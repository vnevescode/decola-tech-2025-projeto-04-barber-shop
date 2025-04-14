package br.com.dio.barber_shop_api.integrationTests.controller;

import br.com.dio.barber_shop_api.controller.request.ScheduleRequest;
import br.com.dio.barber_shop_api.controller.response.ScheduleResponse;
import br.com.dio.barber_shop_api.entity.*;
import br.com.dio.barber_shop_api.repository.*;
import br.com.dio.barber_shop_api.security.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;




@SpringBootTest
@AutoConfigureMockMvc
public class ScheduleControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private IClientRepository clientRepository;

    @Autowired
    private IHaircutTypeRepository haircutTypeRepository;

    @Autowired
    private IScheduleRepository scheduleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IUserRepository userRepository;

    private UUID clientId;
    private UUID haircutId;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        scheduleRepository.deleteAll();
        haircutTypeRepository.deleteAll();
        clientRepository.deleteAll();
        userRepository.deleteAll();

        // Cria um usuário fake para o cliente
        UserEntity user = UserEntity.builder()
                .email("joao@email.com")
                .password("senha123")
                .role(UserEntity.Role.ROLE_USER)
                .build();
        user = userRepository.save(user);

        jwtToken = "Bearer " + jwtService.generateToken(user);

        // Agora o client tem user válido
        ClientEntity client = ClientEntity.builder()
                .name("João Teste")
                .phone("11999999999")
                .user(user)
                .build();
        client = clientRepository.save(client);
        clientId = client.getId();

        HaircutTypeEntity haircut = HaircutTypeEntity.builder()
                .name("Degradê")
                .price(BigDecimal.valueOf(50))
                .build();
        haircut = haircutTypeRepository.save(haircut);
        haircutId = haircut.getId();
    }

    @Test
    void shouldCreateScheduleSuccessfully() throws Exception {
        ScheduleRequest request = new ScheduleRequest(
                OffsetDateTime.now().plusDays(1).withHour(10).withMinute(0),
                OffsetDateTime.now().plusDays(1).withHour(11).withMinute(0),
                clientId,
                haircutId
        );

        mockMvc.perform(post("/schedules")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.clientName").value("João Teste"))
                .andExpect(jsonPath("$.haircutName").value("Degradê"));
    }

    @Test
    void shouldFailToCreateScheduleWhenSlotIsTaken() throws Exception {
        OffsetDateTime start = OffsetDateTime.now().plusDays(1).withHour(12);
        OffsetDateTime end = OffsetDateTime.now().plusDays(1).withHour(13);

        ScheduleRequest request = new ScheduleRequest(start, end, clientId, haircutId);
        scheduleRepository.save(ScheduleEntity.builder()
                .startAt(start)
                .endAt(end)
                .client(clientRepository.findById(clientId).get())
                .haircutType(haircutTypeRepository.findById(haircutId).get())
                .confirmed(false)
                .canceled(false)
                .createdAt(OffsetDateTime.now())
                .build());

        mockMvc.perform(post("/schedules")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Horário já está ocupado"));
    }

    @Test
    void shouldFindSchedulesByMonth() throws Exception {
        OffsetDateTime start = OffsetDateTime.now().withDayOfMonth(1).withHour(10);
        OffsetDateTime end = OffsetDateTime.now().withDayOfMonth(1).withHour(11);

        ScheduleEntity schedule = ScheduleEntity.builder()
                .startAt(start)
                .endAt(end)
                .client(clientRepository.findById(clientId).get())
                .haircutType(haircutTypeRepository.findById(haircutId).get())
                .confirmed(false)
                .canceled(false)
                .createdAt(OffsetDateTime.now())
                .build();

        scheduleRepository.save(schedule);

        mockMvc.perform(get("/schedules/month")
                        .header("Authorization", jwtToken)
                        .param("start", start.minusHours(1).toString())
                        .param("end", end.plusHours(1).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void shouldReturnClientHistoryAndUpcoming() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();

        ScheduleEntity past = ScheduleEntity.builder()
                .startAt(now.minusDays(3))
                .endAt(now.minusDays(3).plusHours(1))
                .client(clientRepository.findById(clientId).get())
                .haircutType(haircutTypeRepository.findById(haircutId).get())
                .confirmed(true)
                .canceled(false)
                .createdAt(now.minusDays(3))
                .build();

        ScheduleEntity future = ScheduleEntity.builder()
                .startAt(now.plusDays(3))
                .endAt(now.plusDays(3).plusHours(1))
                .client(clientRepository.findById(clientId).get())
                .haircutType(haircutTypeRepository.findById(haircutId).get())
                .confirmed(true)
                .canceled(false)
                .createdAt(now)
                .build();

        scheduleRepository.save(past);
        scheduleRepository.save(future);

        mockMvc.perform(get("/schedules/client/" + clientId + "/history")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/schedules/client/" + clientId + "/upcoming")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
