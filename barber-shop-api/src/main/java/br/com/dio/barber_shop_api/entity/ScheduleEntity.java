package br.com.dio.barber_shop_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "tb_schedules",
        uniqueConstraints = @UniqueConstraint(name = "uk_schedule_slot", columnNames = {"start_at", "end_at"})
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ScheduleEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "start_at", nullable = false)
    private OffsetDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private OffsetDateTime endAt;

    @Builder.Default
    @Column(nullable = false)
    private Boolean confirmed = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean canceled = false;

    @Builder.Default
    @Column(name = "created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "haircut_type_id", nullable = false)
    private HaircutTypeEntity haircutType;
}
