package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.model.Product;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {

    @Mapping(target = "width", source = "dto.dimension.width")
    @Mapping(target = "height", source = "dto.dimension.height")
    @Mapping(target = "depth", source = "dto.dimension.depth")
    Product toProduct(NewProductInWarehouseRequest dto);

}
