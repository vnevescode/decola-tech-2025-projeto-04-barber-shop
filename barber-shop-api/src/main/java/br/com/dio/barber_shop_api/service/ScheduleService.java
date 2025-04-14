package br.com.dio.barber_shop_api.service;

import br.com.dio.barber_shop_api.controller.request.ScheduleRequest;
import br.com.dio.barber_shop_api.controller.response.ScheduleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface ScheduleService {
    ScheduleResponse create(ScheduleRequest request);
    List<ScheduleResponse> getAll();
    Page<ScheduleResponse> getAllPageable(Pageable pageable);
    List<ScheduleResponse> findMonth(OffsetDateTime start, OffsetDateTime end);
    List<ScheduleResponse> findClientHistory(UUID clientId);
    List<ScheduleResponse> findClientUpcoming(UUID clientId);
    List<ScheduleResponse> adminFilter(boolean confirmed, boolean canceled, OffsetDateTime start, OffsetDateTime end, UUID haircutTypeId);
    ScheduleResponse confirm(UUID id);
}
