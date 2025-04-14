package br.com.dio.barber_shop_api.unitTests.service;

import br.com.dio.barber_shop_api.controller.request.ScheduleRequest;
import br.com.dio.barber_shop_api.controller.response.ScheduleResponse;
import br.com.dio.barber_shop_api.entity.ClientEntity;
import br.com.dio.barber_shop_api.entity.HaircutTypeEntity;
import br.com.dio.barber_shop_api.entity.ScheduleEntity;
import br.com.dio.barber_shop_api.exception.custom.ResourceNotFoundException;
import br.com.dio.barber_shop_api.exception.custom.ScheduleConflictException;
import br.com.dio.barber_shop_api.mapper.ScheduleMapper;
import br.com.dio.barber_shop_api.repository.IClientRepository;
import br.com.dio.barber_shop_api.repository.IHaircutTypeRepository;
import br.com.dio.barber_shop_api.repository.IScheduleRepository;
import br.com.dio.barber_shop_api.service.impl.ScheduleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class ScheduleServiceImplTest {

    @Mock private IScheduleRepository scheduleRepository;
    @Mock private IClientRepository clientRepository;
    @Mock private IHaircutTypeRepository haircutTypeRepository;
    @Mock private ScheduleMapper scheduleMapper;
    @InjectMocks private ScheduleServiceImpl scheduleService;

    private UUID clientId;
    private UUID haircutId;
    private ScheduleRequest request;
    private OffsetDateTime now;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        now = OffsetDateTime.now();
        clientId = UUID.randomUUID();
        haircutId = UUID.randomUUID();
        request = new ScheduleRequest(
                now.plusHours(1),
                now.plusHours(2),
                clientId,
                haircutId
        );
    }

    /**
     * Deve lançar exceção quando já existir agendamento no horário especificado
     */
    @Test
    void create_WhenSlotIsTaken_ThrowsScheduleConflictException() {
        when(scheduleRepository.existsByStartAtAndEndAt(request.startAt(), request.endAt()))
                .thenReturn(true);

        assertThrows(ScheduleConflictException.class, () -> scheduleService.create(request));
        verify(scheduleRepository).existsByStartAtAndEndAt(request.startAt(), request.endAt());
    }

    /**
     * Deve criar o agendamento corretamente quando os dados forem válidos
     */
    @Test
    void create_WhenValidData_ReturnsScheduleResponse() {
        // Arrange
        ClientEntity client = ClientEntity.builder().id(clientId).name("João").build();
        HaircutTypeEntity haircut = HaircutTypeEntity.builder().id(haircutId).name("Degradê").price(BigDecimal.valueOf(40)).build();
        ScheduleEntity entity = ScheduleEntity.builder().id(UUID.randomUUID()).client(client).haircutType(haircut).build();
        ScheduleResponse response = new ScheduleResponse(entity.getId(), request.startAt(), request.endAt(), false, false, client.getName(), haircut.getName());

        when(scheduleRepository.existsByStartAtAndEndAt(request.startAt(), request.endAt())).thenReturn(false);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(haircutTypeRepository.findById(haircutId)).thenReturn(Optional.of(haircut));
        when(scheduleMapper.toEntity(request)).thenReturn(entity);
        when(scheduleRepository.save(entity)).thenReturn(entity);
        when(scheduleMapper.toResponse(entity)).thenReturn(response);

        // Act
        ScheduleResponse result = scheduleService.create(request);

        // Assert
        assertNotNull(result);
        assertEquals(client.getName(), result.clientName());
        assertEquals(haircut.getName(), result.haircutName());
    }

    /**
     * Deve retornar todos os agendamentos
     */
    @Test
    void getAll_ShouldReturnListOfSchedules() {
        ScheduleEntity entity = ScheduleEntity.builder().id(UUID.randomUUID()).build();
        when(scheduleRepository.findAll()).thenReturn(List.of(entity));
        when(scheduleMapper.toResponseList(any())).thenReturn(List.of(mock(ScheduleResponse.class)));

        List<ScheduleResponse> result = scheduleService.getAll();

        assertEquals(1, result.size());
    }

    /**
     * Deve retornar agendamentos paginados corretamente
     */
    @Test
    void getAllPageable_ShouldReturnPagedSchedules() {
        Pageable pageable = PageRequest.of(0, 10);
        ScheduleEntity entity = ScheduleEntity.builder().id(UUID.randomUUID()).build();
        when(scheduleRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(entity)));
        when(scheduleMapper.toResponse(any())).thenReturn(mock(ScheduleResponse.class));

        Page<ScheduleResponse> result = scheduleService.getAllPageable(pageable);

        assertEquals(1, result.getTotalElements());
    }

    /**
     * Deve retornar agendamentos dentro do intervalo do mês
     */
    @Test
    void findMonth_ShouldReturnSchedulesInDateRange() {
        OffsetDateTime start = now.withDayOfMonth(1);
        OffsetDateTime end = now.withDayOfMonth(30);

        when(scheduleRepository.findByStartAtGreaterThanEqualAndEndAtLessThanEqualOrderByStartAtAscEndAtAsc(start, end))
                .thenReturn(List.of(mock(ScheduleEntity.class)));
        when(scheduleMapper.toResponseList(any())).thenReturn(List.of(mock(ScheduleResponse.class)));

        List<ScheduleResponse> result = scheduleService.findMonth(start, end);

        assertEquals(1, result.size());
    }

    /**
     * Deve retornar o histórico de agendamentos anteriores do cliente
     */
    @Test
    void findClientHistory_ShouldReturnPastSchedules() {
        UUID clientId = UUID.randomUUID();

        when(scheduleRepository.findByClientIdAndStartAtBeforeOrderByStartAtDesc(eq(clientId), any()))
                .thenReturn(List.of(mock(ScheduleEntity.class)));
        when(scheduleMapper.toResponseList(any())).thenReturn(List.of(mock(ScheduleResponse.class)));

        List<ScheduleResponse> result = scheduleService.findClientHistory(clientId);

        assertEquals(1, result.size());
    }

    /**
     * Deve retornar agendamentos futuros não cancelados do cliente
     */
    @Test
    void findClientUpcoming_ShouldReturnUpcomingSchedules() {
        UUID clientId = UUID.randomUUID();

        when(scheduleRepository.findByClientIdAndCanceledFalseAndStartAtAfterOrderByStartAtAsc(eq(clientId), any()))
                .thenReturn(List.of(mock(ScheduleEntity.class)));
        when(scheduleMapper.toResponseList(any())).thenReturn(List.of(mock(ScheduleResponse.class)));

        List<ScheduleResponse> result = scheduleService.findClientUpcoming(clientId);

        assertEquals(1, result.size());
    }

    /**
     * Deve aplicar corretamente os filtros combinados para admin
     */
    @Test
    void adminFilter_ShouldReturnFilteredSchedules() {
        UUID haircutTypeId = UUID.randomUUID();
        OffsetDateTime start = now;
        OffsetDateTime end = now.plusDays(7);

        when(scheduleRepository.findByConfirmedAndCanceledAndStartAtBetweenAndHaircutType_Id(true, false, start, end, haircutTypeId))
                .thenReturn(List.of(mock(ScheduleEntity.class)));
        when(scheduleMapper.toResponseList(any())).thenReturn(List.of(mock(ScheduleResponse.class)));

        List<ScheduleResponse> result = scheduleService.adminFilter(true, false, start, end, haircutTypeId);

        assertEquals(1, result.size());
    }

    /**
     * Deve lançar exceção se cliente não for encontrado ao criar
     */
    @Test
    void create_WhenClientNotFound_ThrowsException() {
        when(scheduleRepository.existsByStartAtAndEndAt(request.startAt(), request.endAt())).thenReturn(false);
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> scheduleService.create(request));
    }

    /**
     * Deve lançar exceção se tipo de corte não for encontrado ao criar
     */
    @Test
    void create_WhenHaircutNotFound_ThrowsException() {
        ClientEntity client = ClientEntity.builder().id(clientId).build();
        when(scheduleRepository.existsByStartAtAndEndAt(request.startAt(), request.endAt())).thenReturn(false);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(haircutTypeRepository.findById(haircutId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> scheduleService.create(request));
    }
}
