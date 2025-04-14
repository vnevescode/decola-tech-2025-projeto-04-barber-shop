package br.com.dio.barber_shop_api.controller;

import br.com.dio.barber_shop_api.controller.doc.ScheduleControllerDoc;
import br.com.dio.barber_shop_api.controller.request.ScheduleRequest;
import br.com.dio.barber_shop_api.controller.response.ScheduleResponse;
import br.com.dio.barber_shop_api.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.data.domain.Pageable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController implements ScheduleControllerDoc {

    private final ScheduleService scheduleService;

    @Override
    @PostMapping
    public ScheduleResponse create(@RequestBody @Valid ScheduleRequest request) {
        log.info("POST /schedules | clientId={}, haircutTypeId={}, startAt={}", request.clientId(), request.haircutTypeId(), request.startAt());
        return scheduleService.create(request);
    }

    @Override
    @GetMapping
    public List<ScheduleResponse> getAll() {
        log.info("GET /schedules");
        return scheduleService.getAll();
    }

    @Override
    @GetMapping("/paged")
    public Page<ScheduleResponse> getAllPageable(Pageable pageable) {
        log.info("GET /schedules/paged | page: {}", pageable.getPageNumber());
        return scheduleService.getAllPageable(pageable);
    }

    @Override
    @GetMapping("/month")
    public List<ScheduleResponse> findMonth(
            @RequestParam OffsetDateTime start,
            @RequestParam OffsetDateTime end
    ) {
        log.info("GET /schedules/month | start={}, end={}", start, end);
        return scheduleService.findMonth(start, end);
    }

    @Override
    @GetMapping("/client/{clientId}/history")
    public List<ScheduleResponse> findClientHistory(@PathVariable UUID clientId) {
        log.info("GET /schedules/history/{}", clientId);
        return scheduleService.findClientHistory(clientId);
    }


    @Override
    @GetMapping("/client/{clientId}/upcoming")
    public List<ScheduleResponse> findClientUpcoming(@PathVariable UUID clientId) {
        log.info("GET /schedules/upcoming/{}", clientId);
        return scheduleService.findClientUpcoming(clientId);
    }

    @Override
    @GetMapping("/admin-filter")
    public List<ScheduleResponse> adminFilter(
            @RequestParam boolean confirmed,
            @RequestParam boolean canceled,
            @RequestParam OffsetDateTime start,
            @RequestParam OffsetDateTime end,
            @RequestParam UUID haircutTypeId
    ) {
        log.info("GET /schedules/admin-filter | confirmed={}, canceled={}, haircutTypeId={}", confirmed, canceled, haircutTypeId);
        return scheduleService.adminFilter(confirmed, canceled, start, end, haircutTypeId);
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    public ScheduleResponse confirm(@PathVariable UUID id) {
        log.info("PATCH /schedules/{}/confirm", id);
        return scheduleService.confirm(id);
    }
}
