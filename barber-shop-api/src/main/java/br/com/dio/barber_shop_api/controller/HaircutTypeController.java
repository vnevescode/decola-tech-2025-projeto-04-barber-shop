package br.com.dio.barber_shop_api.controller;

import br.com.dio.barber_shop_api.controller.doc.HaircutTypeControllerDoc;
import br.com.dio.barber_shop_api.controller.request.HaircutTypeRequest;
import br.com.dio.barber_shop_api.controller.response.HaircutTypeResponse;
import br.com.dio.barber_shop_api.service.HaircutTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/haircuts")
@RequiredArgsConstructor
public class HaircutTypeController implements HaircutTypeControllerDoc {
    private final HaircutTypeService haircutTypeService;

    @Override
    @PostMapping
    public HaircutTypeResponse create(@RequestBody @Valid HaircutTypeRequest request) {
        log.info("POST /haircut-types | creating haircut type: {}", request.name());
        return haircutTypeService.create(request);
    }

    @Override
    @GetMapping
    public List<HaircutTypeResponse> getAll() {
        log.info("GET /haircut-types");
        return haircutTypeService.getAll();
    }

    @Override
    @GetMapping("/paged")
    public Page<HaircutTypeResponse> getAllPageable(Pageable pageable) {
        log.info("GET /haircut-types/paged | page: {}", pageable.getPageNumber());
        return haircutTypeService.getAllPageable(pageable);
    }

    @Override
    @GetMapping("/exists")
    public boolean existsByName(@RequestParam String name) {
        return haircutTypeService.existsByName(name);
    }
}
