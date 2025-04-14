package br.com.dio.barber_shop_api.controller.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ScheduleResponse(
        UUID id,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        boolean confirmed,
        boolean canceled,
        String clientName,
        String haircutName
) {}
