package br.com.dio.barber_shop_api.mapper;

import br.com.dio.barber_shop_api.common.sanitizer.SanitizerMapper;
import br.com.dio.barber_shop_api.controller.request.ClientRequest;
import br.com.dio.barber_shop_api.controller.response.ClientResponse;
import br.com.dio.barber_shop_api.entity.ClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = SanitizerMapper.class)
public interface ClientMapper {

    @Mapping(target = "name", source = "name", qualifiedByName = "sanitize")
    @Mapping(target = "phone", source = "phone", qualifiedByName = "sanitize")
    @Mapping(target = "id", ignore = true) // Será setado pelo serviço ao atualizar
    @Mapping(target = "user", ignore = true) // Injetado pela camada de serviço
    ClientEntity toEntity(ClientRequest request);

    ClientResponse toResponse(ClientEntity entity);

    List<ClientResponse> toResponseList(List<ClientEntity> entities);
}
