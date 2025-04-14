package br.com.dio.barber_shop_api.service;

import br.com.dio.barber_shop_api.controller.request.HaircutTypeRequest;
import br.com.dio.barber_shop_api.controller.response.HaircutTypeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HaircutTypeService {
    HaircutTypeResponse create(HaircutTypeRequest request);
    List<HaircutTypeResponse> getAll();
    Page<HaircutTypeResponse> getAllPageable(Pageable pageable);
    boolean existsByName(String name);
}
