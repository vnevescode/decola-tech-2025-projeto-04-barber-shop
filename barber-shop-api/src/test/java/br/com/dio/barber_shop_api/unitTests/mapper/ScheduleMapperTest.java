package br.com.dio.barber_shop_api.unitTests.mapper;

import br.com.dio.barber_shop_api.controller.request.ScheduleRequest;
import br.com.dio.barber_shop_api.controller.response.ScheduleResponse;
import br.com.dio.barber_shop_api.entity.ClientEntity;
import br.com.dio.barber_shop_api.entity.HaircutTypeEntity;
import br.com.dio.barber_shop_api.entity.ScheduleEntity;
import br.com.dio.barber_shop_api.mapper.ScheduleMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class ScheduleMapperTest {

    private ScheduleMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new ScheduleMapperImpl();
    }

    /**
     * 🧪 Testa o mapeamento do DTO ScheduleRequest para a entidade ScheduleEntity
     */
    @Test
    void toEntity_ShouldMapRequestCorrectly() {
        ScheduleRequest request = new ScheduleRequest(
                OffsetDateTime.now(),
                OffsetDateTime.now().plusHours(1),
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        ScheduleEntity entity = mapper.toEntity(request);

        assertEquals(request.startAt(), entity.getStartAt());
        assertEquals(request.endAt(), entity.getEndAt());
    }

    /**
     * 🧪 Testa o mapeamento da entidade ScheduleEntity para o DTO ScheduleResponse
     *      - Inclui os nomes do cliente e do corte
     */
    @Test
    void toResponse_ShouldIncludeClientAndHaircutNames() {
        UUID id = UUID.randomUUID();
        ScheduleEntity entity = ScheduleEntity.builder()
                .id(id)
                .startAt(OffsetDateTime.now())
                .endAt(OffsetDateTime.now().plusHours(1))
                .confirmed(true)
                .canceled(false)
                .client(ClientEntity.builder().name("João").build())
                .haircutType(HaircutTypeEntity.builder().name("Fade").build())
                .build();

        ScheduleResponse response = mapper.toResponse(entity);

        assertEquals("João", response.clientName());
        assertEquals("Fade", response.haircutName());
    }


}
