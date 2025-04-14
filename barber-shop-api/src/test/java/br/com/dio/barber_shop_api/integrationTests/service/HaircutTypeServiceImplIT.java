package br.com.dio.barber_shop_api.integrationTests.service;

import br.com.dio.barber_shop_api.controller.request.HaircutTypeRequest;
import br.com.dio.barber_shop_api.controller.response.HaircutTypeResponse;
import br.com.dio.barber_shop_api.repository.IHaircutTypeRepository;
import br.com.dio.barber_shop_api.repository.IScheduleRepository;
import br.com.dio.barber_shop_api.service.HaircutTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class HaircutTypeServiceImplIT {

    @Autowired
    private HaircutTypeService haircutTypeService;

    @Autowired
    private IHaircutTypeRepository haircutTypeRepository;

    @Autowired
    private IScheduleRepository scheduleRepository;

    private HaircutTypeRequest sampleRequest;

    @BeforeEach
    void setup() {
        scheduleRepository.deleteAll();
        haircutTypeRepository.deleteAll();
        sampleRequest = new HaircutTypeRequest("Corte Navalhado", BigDecimal.valueOf(50));
    }

    @Test
    @DisplayName("✅ Deve criar tipo de corte com sucesso")
    void shouldCreateHaircutTypeSuccessfully() {
        HaircutTypeResponse response = haircutTypeService.create(sampleRequest);

        assertNotNull(response);
        assertEquals("Corte Navalhado", response.name());
        assertTrue(haircutTypeRepository.existsByName("Corte Navalhado"));
    }

    @Test
    @DisplayName("📋 Deve retornar todos os tipos de corte")
    void shouldReturnAllHaircutTypes() {
        haircutTypeService.create(sampleRequest);
        haircutTypeService.create(new HaircutTypeRequest("Corte Social", BigDecimal.valueOf(40)));

        List<HaircutTypeResponse> list = haircutTypeService.getAll();
        assertEquals(2, list.size());
    }

    @Test
    @DisplayName("📑 Deve retornar tipos de corte paginados")
    void shouldReturnPagedHaircutTypes() {
        haircutTypeService.create(sampleRequest);
        haircutTypeService.create(new HaircutTypeRequest("Corte Moicano", BigDecimal.valueOf(35)));

        Pageable pageable = PageRequest.of(0, 1);
        var page = haircutTypeService.getAllPageable(pageable);

        assertEquals(1, page.getSize());
        assertEquals(2, page.getTotalElements());
    }

    @Test
    @DisplayName("🔍 Deve verificar existência de tipo de corte por nome")
    void shouldCheckIfHaircutTypeExistsByName() {
        haircutTypeService.create(sampleRequest);

        assertTrue(haircutTypeService.existsByName("Corte Navalhado"));
        assertFalse(haircutTypeService.existsByName("Corte Samurai"));
    }
}
