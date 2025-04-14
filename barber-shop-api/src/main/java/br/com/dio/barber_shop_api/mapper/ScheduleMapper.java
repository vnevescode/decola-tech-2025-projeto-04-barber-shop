package br.com.dio.barber_shop_api.mapper;

import br.com.dio.barber_shop_api.controller.request.ScheduleRequest;
import br.com.dio.barber_shop_api.controller.response.ScheduleResponse;
import br.com.dio.barber_shop_api.entity.ScheduleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ScheduleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "confirmed", ignore = true)
    @Mapping(target = "canceled", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "haircutType", ignore = true)
    ScheduleEntity toEntity(ScheduleRequest request);

    @Mapping(target = "clientName", source = "client.name")
    @Mapping(target = "haircutName", source = "haircutType.name")
    ScheduleResponse toResponse(ScheduleEntity entity);

    List<ScheduleResponse> toResponseList(List<ScheduleEntity> entities);
}