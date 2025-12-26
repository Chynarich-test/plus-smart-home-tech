package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewProductInWarehouseRequest {
    @NotNull(message = "Айди не может быть пустым")
    private UUID productId;
    private Boolean fragile;
    @NotNull(message = "Размеры товара не могут быть пустыми")
    private DimensionDto dimension;
    @NotNull(message = "Вес товара не может быть пустым")
    private Double weight;
}
