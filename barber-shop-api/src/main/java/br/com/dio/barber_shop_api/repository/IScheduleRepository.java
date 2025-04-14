package br.com.dio.barber_shop_api.repository;

import br.com.dio.barber_shop_api.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface IScheduleRepository extends JpaRepository<ScheduleEntity, UUID> {
    boolean existsByStartAtAndEndAt(OffsetDateTime startAt, OffsetDateTime endAt);

    // Agendamentos por mês
    List<ScheduleEntity> findByStartAtGreaterThanEqualAndEndAtLessThanEqualOrderByStartAtAscEndAtAsc(
            OffsetDateTime startAt,
            OffsetDateTime endAt
    );

    // Histórico do cliente
    List<ScheduleEntity> findByClientIdAndStartAtBeforeOrderByStartAtDesc(UUID clientId, OffsetDateTime now);


    // Próximos agendamentos (não cancelados)
    List<ScheduleEntity> findByClientIdAndCanceledFalseAndStartAtAfterOrderByStartAtAsc(UUID clientId, OffsetDateTime now);

    // Filtros combinados (ADMIN)
    List<ScheduleEntity> findByConfirmedAndCanceledAndStartAtBetweenAndHaircutType_Id(
            boolean confirmed,
            boolean canceled,
            OffsetDateTime start,
            OffsetDateTime end,
            UUID haircutTypeId
    );
}
