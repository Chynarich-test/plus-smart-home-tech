package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.model.ShoppingCart;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {

    @Mapping(target = "username", ignore = true)
    @Mapping(target = "state", ignore = true)
    ShoppingCart toModel(ShoppingCartDto dto);

    ShoppingCartDto toDto(ShoppingCart model);
}
