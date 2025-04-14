package br.com.dio.barber_shop_api.service.impl;

import br.com.dio.barber_shop_api.controller.request.HaircutTypeRequest;
import br.com.dio.barber_shop_api.controller.response.HaircutTypeResponse;
import br.com.dio.barber_shop_api.entity.HaircutTypeEntity;
import br.com.dio.barber_shop_api.mapper.HaircutTypeMapper;
import br.com.dio.barber_shop_api.repository.IHaircutTypeRepository;
import br.com.dio.barber_shop_api.service.HaircutTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class HaircutTypeServiceImpl implements HaircutTypeService {

    private final IHaircutTypeRepository repository;
    private final HaircutTypeMapper mapper;

    @Override
    public HaircutTypeResponse create(HaircutTypeRequest request) {
        log.info("Creating haircut type with name: {}", request.name());
        HaircutTypeEntity entity = mapper.toEntity(request);
        HaircutTypeResponse response = mapper.toResponse(repository.save(entity));
        log.info("Haircut type created with ID: {}", response.id());
        return response;
    }

    @Override
    public List<HaircutTypeResponse> getAll() {
        log.info("Retrieving all haircut types");
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    public Page<HaircutTypeResponse> getAllPageable(Pageable pageable) {
        log.info("Retrieving paged haircut types");
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    public boolean existsByName(String name) {
        log.info("Checking if haircut type exists with name: {}", name);
        return repository.existsByName(name);
    }
}
