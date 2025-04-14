package br.com.dio.barber_shop_api.unitTests.mapper;

import br.com.dio.barber_shop_api.common.sanitizer.SanitizerMapper;
import br.com.dio.barber_shop_api.controller.request.ClientRequest;
import br.com.dio.barber_shop_api.controller.response.ClientResponse;
import br.com.dio.barber_shop_api.entity.ClientEntity;
import br.com.dio.barber_shop_api.mapper.ClientMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class ClientMapperTest {

    private ClientMapperImpl mapper;

    @BeforeEach
    void setUp() throws Exception {
        mapper = new ClientMapperImpl();

        // ⚙️ Injeta o SanitizerMapper no ClientMapperImpl usando reflexão (sem Spring)
        Field sanitizerField = ClientMapperImpl.class.getDeclaredField("sanitizerMapper");
        sanitizerField.setAccessible(true);
        sanitizerField.set(mapper, new SanitizerMapper());
    }

    /**
     * Deve mapear corretamente ClientRequest para ClientEntity com sanitização
     */
    @Test
    void toEntity_ShouldMapCorrectlyAndSanitize() {
        UUID fakeUserId = UUID.randomUUID();
        ClientRequest request = new ClientRequest("<b>João</b>", "<script>12345678901</script>", fakeUserId);

        ClientEntity entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertEquals("João", entity.getName());
        assertFalse(entity.getPhone().contains("script"));
        //assertEquals(fakeUserId, entity.getUser().getId());
    }

    /**
     * Deve mapear ClientEntity para ClientResponse corretamente
     */
    @Test
    void toResponse_ShouldMapCorrectly() {
        ClientEntity entity = ClientEntity.builder()
                .id(UUID.randomUUID())
                .name("Maria")
                .phone("11999999999")
                .build();

        ClientResponse response = mapper.toResponse(entity);

        assertEquals("Maria", response.name());
        assertEquals("11999999999", response.phone());
    }


}
