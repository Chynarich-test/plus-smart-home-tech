package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookedProductsDto {
    @NotNull(message = "Общий вес доставки не может быть пустым")
    @Builder.Default
    private Double deliveryWeight = 0d;
    @NotNull(message = "Общий объем доставки не может быть пустым")
    @Builder.Default
    private Double deliveryVolume = 0d;
    @NotNull(message = "Параметр хрупкости не может быть пустым")
    @Builder.Default
    private Boolean fragile = false;
}
