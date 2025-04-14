package br.com.dio.barber_shop_api.mapper;

import br.com.dio.barber_shop_api.controller.response.UserResponse;
import br.com.dio.barber_shop_api.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserResponse toResponse(UserEntity entity);
}

