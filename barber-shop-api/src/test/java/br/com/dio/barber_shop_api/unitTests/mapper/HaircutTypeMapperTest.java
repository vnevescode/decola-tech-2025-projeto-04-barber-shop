package br.com.dio.barber_shop_api.unitTests.mapper;

import br.com.dio.barber_shop_api.common.sanitizer.SanitizerMapper;
import br.com.dio.barber_shop_api.controller.request.HaircutTypeRequest;
import br.com.dio.barber_shop_api.controller.response.HaircutTypeResponse;
import br.com.dio.barber_shop_api.entity.HaircutTypeEntity;
import br.com.dio.barber_shop_api.mapper.HaircutTypeMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class HaircutTypeMapperTest {

    private HaircutTypeMapperImpl mapper;

    @BeforeEach
    void setUp() throws Exception {
        mapper = new HaircutTypeMapperImpl();

        // ⚙️ Injeta o SanitizerMapper no HaircutTypeMapperImpl usando reflexão
        Field sanitizerField = HaircutTypeMapperImpl.class.getDeclaredField("sanitizerMapper");
        sanitizerField.setAccessible(true);
        sanitizerField.set(mapper, new SanitizerMapper());
    }

    /**
     * 🧪 Testa o mapeamento do DTO HaircutTypeRequest para a entidade HaircutTypeEntity
     *      - Remove HTML usando a sanitização
     */
    @Test
    void toEntity_ShouldSanitizeName() {
        HaircutTypeRequest request = new HaircutTypeRequest("<b>Moicano</b>", BigDecimal.valueOf(50));
        HaircutTypeEntity entity = mapper.toEntity(request);

        assertEquals("Moicano", entity.getName());
        assertEquals(BigDecimal.valueOf(50), entity.getPrice());
    }

    /**
     * 🧪 Testa o mapeamento da entidade HaircutTypeEntity para o DTO HaircutTypeResponse
     */
    @Test
    void toResponse_ShouldMapCorrectly() {
        UUID id = UUID.randomUUID();
        HaircutTypeEntity entity = new HaircutTypeEntity(id, "Corte", BigDecimal.TEN);
        HaircutTypeResponse response = mapper.toResponse(entity);

        assertEquals(id, response.id());
        assertEquals("Corte", response.name());
    }
}
