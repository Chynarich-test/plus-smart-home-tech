package ru.yandex.practicum.dto.product;

import jakarta.validation.constraints.NotBlank;
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
public class SetProductQuantityStateRequest {
    @NotNull(message = "Идентификатор товара не может быть пустым")
    private UUID productId;
    @NotNull(message = "Количество товара не может быть пустым")
    private QuantityState quantityState;
}
