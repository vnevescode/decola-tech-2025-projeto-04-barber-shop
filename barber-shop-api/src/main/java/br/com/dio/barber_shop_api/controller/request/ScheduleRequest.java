package br.com.dio.barber_shop_api.controller.request;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ScheduleRequest(
        @NotNull OffsetDateTime startAt,
        @NotNull OffsetDateTime endAt,
        @NotNull UUID clientId,
        @NotNull UUID haircutTypeId
) {}
