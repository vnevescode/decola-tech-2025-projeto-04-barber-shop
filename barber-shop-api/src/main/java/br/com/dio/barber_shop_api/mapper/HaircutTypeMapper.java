package br.com.dio.barber_shop_api.mapper;

import br.com.dio.barber_shop_api.common.sanitizer.SanitizerMapper;
import br.com.dio.barber_shop_api.controller.request.HaircutTypeRequest;
import br.com.dio.barber_shop_api.controller.response.HaircutTypeResponse;
import br.com.dio.barber_shop_api.entity.HaircutTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = SanitizerMapper.class )
public interface HaircutTypeMapper {

    @Mapping(target = "name", source = "name", qualifiedByName = "sanitize")
    @Mapping(target = "id", ignore = true)
    HaircutTypeEntity toEntity(HaircutTypeRequest request);

    HaircutTypeResponse toResponse(HaircutTypeEntity entity);

    List<HaircutTypeResponse> toResponseList(List<HaircutTypeEntity> entities);
}
