package br.com.dio.barber_shop_api.integrationTests.service;

import br.com.dio.barber_shop_api.controller.request.ScheduleRequest;
import br.com.dio.barber_shop_api.controller.response.ScheduleResponse;
import br.com.dio.barber_shop_api.entity.ClientEntity;
import br.com.dio.barber_shop_api.entity.HaircutTypeEntity;
import br.com.dio.barber_shop_api.entity.ScheduleEntity;
import br.com.dio.barber_shop_api.entity.UserEntity;
import br.com.dio.barber_shop_api.exception.custom.ScheduleConflictException;
import br.com.dio.barber_shop_api.repository.IClientRepository;
import br.com.dio.barber_shop_api.repository.IHaircutTypeRepository;
import br.com.dio.barber_shop_api.repository.IScheduleRepository;
import br.com.dio.barber_shop_api.repository.IUserRepository;
import br.com.dio.barber_shop_api.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ScheduleServiceImplIT {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private IClientRepository clientRepository;

    @Autowired
    private IHaircutTypeRepository haircutTypeRepository;

    @Autowired
    private IScheduleRepository scheduleRepository;

    @Autowired
    private IUserRepository userRepository;

    private ClientEntity client;
    private HaircutTypeEntity haircut;

    @BeforeEach
    void setup() {

        // Cria um usuário fictício com email e role (sem username)
        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .email("test-user@example.com")
                .password("secure-password")
                .role(UserEntity.Role.ROLE_USER) // Usando o enum interno da própria classe
                .build();
        user = userRepository.save(user);

        String uniquePhone = "119" + UUID.randomUUID().toString().substring(0, 8);

        // Associa o user ao cliente de teste
        client = clientRepository.save(ClientEntity.builder()
                .name("João Teste")
                .phone(uniquePhone)
                .user(user) // Agora corretamente passando um UserEntity
                .build());

        haircut = haircutTypeRepository.save(HaircutTypeEntity.builder()
                .name("Corte Teste")
                .price(BigDecimal.valueOf(35.0))
                .build());
    }

    @Test
    @DisplayName("✅ Deve criar agendamento com sucesso")
    void shouldCreateScheduleSuccessfully() {
        OffsetDateTime start = OffsetDateTime.now().plusDays(1);
        OffsetDateTime end = start.plusHours(1);

        ScheduleRequest request = new ScheduleRequest(start, end, client.getId(), haircut.getId());
        ScheduleResponse response = scheduleService.create(request);

        assertNotNull(response);
        assertEquals("João Teste", response.clientName());
    }

    @Test
    @DisplayName("🚫 Deve lançar exceção se horário já estiver ocupado")
    void shouldThrowWhenSlotIsTaken() {
        OffsetDateTime start = OffsetDateTime.now().plusDays(1);
        OffsetDateTime end = start.plusHours(1);

        ScheduleRequest request = new ScheduleRequest(start, end, client.getId(), haircut.getId());
        scheduleService.create(request);

        assertThrows(ScheduleConflictException.class, () -> scheduleService.create(request));
    }

    @Test
    @DisplayName("📅 Deve buscar histórico de agendamentos passados do cliente")
    void shouldReturnClientHistory() {
        OffsetDateTime start = OffsetDateTime.now().minusDays(2);
        OffsetDateTime end = start.plusHours(1);

        scheduleRepository.save(ScheduleEntity.builder()
                .startAt(start)
                .endAt(end)
                .confirmed(true)
                .canceled(false)
                .client(client)
                .haircutType(haircut)
                .build());

        List<ScheduleResponse> history = scheduleService.findClientHistory(client.getId());
        assertFalse(history.isEmpty());
    }

    @Test
    @DisplayName("🔜 Deve retornar próximos agendamentos do cliente")
    void shouldReturnClientUpcoming() {
        OffsetDateTime start = OffsetDateTime.now().plusDays(2);
        OffsetDateTime end = start.plusHours(1);

        scheduleRepository.save(ScheduleEntity.builder()
                .startAt(start)
                .endAt(end)
                .confirmed(false)
                .canceled(false)
                .client(client)
                .haircutType(haircut)
                .build());

        List<ScheduleResponse> upcoming = scheduleService.findClientUpcoming(client.getId());
        assertFalse(upcoming.isEmpty());
    }

    @Test
    @DisplayName("📆 Deve buscar agendamentos do mês corretamente")
    void shouldReturnMonthSchedules() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime start = now.withDayOfMonth(5);
        OffsetDateTime end = start.plusHours(1);

        scheduleRepository.save(ScheduleEntity.builder()
                .startAt(start)
                .endAt(end)
                .client(client)
                .haircutType(haircut)
                .confirmed(false)
                .canceled(false)
                .build());

        OffsetDateTime monthStart = now.withDayOfMonth(1).withHour(0).withMinute(0);
        OffsetDateTime monthEnd = now.withDayOfMonth(now.getMonth().length(now.toLocalDate().isLeapYear()))
                .withHour(23).withMinute(59);

        List<ScheduleResponse> schedules = scheduleService.findMonth(monthStart, monthEnd);
        assertFalse(schedules.isEmpty());
    }

    @Test
    @DisplayName("🧪 Deve aplicar filtro admin em agendamentos com sucesso")
    void shouldFilterSchedulesWithAdminFilters() {
        OffsetDateTime start = OffsetDateTime.now().plusDays(3);
        OffsetDateTime end = start.plusHours(1);

        scheduleRepository.save(ScheduleEntity.builder()
                .startAt(start)
                .endAt(end)
                .client(client)
                .haircutType(haircut)
                .confirmed(true)
                .canceled(false)
                .build());

        List<ScheduleResponse> filtered = scheduleService.adminFilter(true, false,
                start.minusDays(1), end.plusDays(1), haircut.getId());

        assertFalse(filtered.isEmpty());
    }

    @Test
    @DisplayName("📄 Deve retornar todos os agendamentos")
    void shouldReturnAllSchedules() {
        OffsetDateTime start = OffsetDateTime.now().plusDays(5);
        OffsetDateTime end = start.plusHours(1);

        scheduleRepository.save(ScheduleEntity.builder()
                .startAt(start)
                .endAt(end)
                .client(client)
                .haircutType(haircut)
                .confirmed(true)
                .canceled(false)
                .build());

        List<ScheduleResponse> allSchedules = scheduleService.getAll();
        assertFalse(allSchedules.isEmpty());
    }

    @Test
    @DisplayName("📑 Deve retornar agendamentos paginados")
    void shouldReturnPagedSchedules() {
        OffsetDateTime start = OffsetDateTime.now().plusDays(6);
        OffsetDateTime end = start.plusHours(1);

        scheduleRepository.save(ScheduleEntity.builder()
                .startAt(start)
                .endAt(end)
                .client(client)
                .haircutType(haircut)
                .confirmed(false)
                .canceled(false)
                .build());

        Page<ScheduleResponse> page = scheduleService.getAllPageable(PageRequest.of(0, 10));
        assertFalse(page.isEmpty());
    }
}
