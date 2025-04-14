package br.com.dio.barber_shop_api.service.impl;

import br.com.dio.barber_shop_api.controller.request.ScheduleRequest;
import br.com.dio.barber_shop_api.controller.response.ScheduleResponse;
import br.com.dio.barber_shop_api.entity.ClientEntity;
import br.com.dio.barber_shop_api.entity.HaircutTypeEntity;
import br.com.dio.barber_shop_api.entity.ScheduleEntity;
import br.com.dio.barber_shop_api.exception.custom.ResourceNotFoundException;
import br.com.dio.barber_shop_api.exception.custom.ScheduleConflictException;
import br.com.dio.barber_shop_api.mapper.ScheduleMapper;
import br.com.dio.barber_shop_api.repository.IClientRepository;
import br.com.dio.barber_shop_api.repository.IHaircutTypeRepository;
import br.com.dio.barber_shop_api.repository.IScheduleRepository;
import br.com.dio.barber_shop_api.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final IScheduleRepository scheduleRepository;
    private final IClientRepository clientRepository;
    private final IHaircutTypeRepository haircutTypeRepository;
    private final ScheduleMapper scheduleMapper;

    @Override
    public ScheduleResponse create(ScheduleRequest request) {
        log.info("Creating schedule for clientId: {} and haircutTypeId: {}", request.clientId(), request.haircutTypeId());

        if (scheduleRepository.existsByStartAtAndEndAt(request.startAt(), request.endAt())) {
            log.warn("Schedule conflict: {} - {}", request.startAt(), request.endAt());
            throw new ScheduleConflictException("Horário já está ocupado");
        }

        ClientEntity client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        HaircutTypeEntity haircut = haircutTypeRepository.findById(request.haircutTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de corte não encontrado"));

        ScheduleEntity entity = scheduleMapper.toEntity(request);
        entity.setClient(client);
        entity.setHaircutType(haircut);
        entity.setConfirmed(false);
        entity.setCanceled(false);
        entity.setCreatedAt(OffsetDateTime.now());

        ScheduleResponse response = scheduleMapper.toResponse(scheduleRepository.save(entity));
        log.info("Schedule created with ID: {}", response.id());
        return response;
    }

    @Override
    public List<ScheduleResponse> getAll() {
        log.info("Retrieving all schedules");
        return scheduleMapper.toResponseList(scheduleRepository.findAll());
    }

    @Override
    public Page<ScheduleResponse> getAllPageable(Pageable pageable) {
        log.info("Retrieving paged schedules");
        return scheduleRepository.findAll(pageable).map(scheduleMapper::toResponse);
    }

    @Override
    public List<ScheduleResponse> findMonth(OffsetDateTime start, OffsetDateTime end) {
        log.info("Finding schedules between {} and {}", start, end);
        return scheduleMapper.toResponseList(
                scheduleRepository.findByStartAtGreaterThanEqualAndEndAtLessThanEqualOrderByStartAtAscEndAtAsc(start, end));
    }

    @Override
    public List<ScheduleResponse> findClientHistory(UUID clientId) {
        log.info("Finding past schedules for clientId: {}", clientId);
        return scheduleMapper.toResponseList(
                scheduleRepository.findByClientIdAndStartAtBeforeOrderByStartAtDesc(clientId, OffsetDateTime.now()));
    }

    @Override
    public List<ScheduleResponse> findClientUpcoming(UUID clientId) {
        log.info("Finding upcoming schedules for clientId: {}", clientId);
        return scheduleMapper.toResponseList(
                scheduleRepository.findByClientIdAndCanceledFalseAndStartAtAfterOrderByStartAtAsc(clientId, OffsetDateTime.now()));
    }

    @Override
    public List<ScheduleResponse> adminFilter(boolean confirmed, boolean canceled, OffsetDateTime start, OffsetDateTime end, UUID haircutTypeId) {
        log.info("Admin filter on schedules [confirmed: {}, canceled: {}, start: {}, end: {}, haircutTypeId: {}]",
                confirmed, canceled, start, end, haircutTypeId);
        return scheduleMapper.toResponseList(
                scheduleRepository.findByConfirmedAndCanceledAndStartAtBetweenAndHaircutType_Id(confirmed, canceled, start, end, haircutTypeId));
    }

    @Override
    public ScheduleResponse confirm(UUID id) {
        ScheduleEntity schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado"));

        schedule.setConfirmed(true);
        log.info("Admin confirmou o agendamento com ID: {}", id);

        return scheduleMapper.toResponse(scheduleRepository.save(schedule));
    }
}