package br.com.dio.barber_shop_api.unitTests.service;

import br.com.dio.barber_shop_api.controller.request.HaircutTypeRequest;
import br.com.dio.barber_shop_api.controller.response.HaircutTypeResponse;
import br.com.dio.barber_shop_api.entity.HaircutTypeEntity;
import br.com.dio.barber_shop_api.mapper.HaircutTypeMapper;
import br.com.dio.barber_shop_api.repository.IHaircutTypeRepository;
import br.com.dio.barber_shop_api.service.impl.HaircutTypeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HaircutTypeServiceImplTest {

    @Mock private IHaircutTypeRepository repository;
    @Mock private HaircutTypeMapper mapper;

    @InjectMocks private HaircutTypeServiceImpl service;

    private HaircutTypeRequest request;
    private HaircutTypeEntity entity;
    private HaircutTypeResponse response;
    private UUID haircutId;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        haircutId = UUID.randomUUID();

        request = new HaircutTypeRequest("Corte Social", BigDecimal.valueOf(45.00));
        entity = HaircutTypeEntity.builder()
                .id(haircutId)
                .name("Corte Social")
                .price(BigDecimal.valueOf(45.00))
                .build();

        response = new HaircutTypeResponse(haircutId, "Corte Social", BigDecimal.valueOf(45.00));
    }

    /**
     * Deve criar um novo tipo de corte com sucesso
     */
    @Test
    void create_ShouldReturnHaircutTypeResponse() {
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        HaircutTypeResponse result = service.create(request);

        assertNotNull(result);
        assertEquals("Corte Social", result.name());
        assertEquals(BigDecimal.valueOf(45.00), result.price());
        verify(repository).save(entity);
    }

    /**
     * Deve retornar a lista completa de tipos de corte
     */
    @Test
    void getAll_ShouldReturnListOfHaircuts() {
        List<HaircutTypeEntity> entityList = List.of(entity);
        List<HaircutTypeResponse> responseList = List.of(response);

        when(repository.findAll()).thenReturn(entityList);
        when(mapper.toResponseList(entityList)).thenReturn(responseList);

        List<HaircutTypeResponse> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals("Corte Social", result.get(0).name());
    }

    /**
     * Deve retornar os tipos de corte de forma paginada
     */
    @Test
    void getAllPageable_ShouldReturnPagedHaircuts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<HaircutTypeEntity> page = new PageImpl<>(List.of(entity));

        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.toResponse(entity)).thenReturn(response);

        Page<HaircutTypeResponse> result = service.getAllPageable(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Corte Social", result.getContent().get(0).name());
    }

    /**
     * Deve retornar true quando o nome já existir
     */
    @Test
    void existsByName_WhenExists_ShouldReturnTrue() {
        when(repository.existsByName("Corte Social")).thenReturn(true);

        boolean result = service.existsByName("Corte Social");

        assertTrue(result);
    }

    /**
     * Deve retornar false quando o nome não existir
     */
    @Test
    void existsByName_WhenNotExists_ShouldReturnFalse() {
        when(repository.existsByName("Corte Moicano")).thenReturn(false);

        boolean result = service.existsByName("Corte Moicano");

        assertFalse(result);
    }
}
