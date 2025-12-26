package ru.yandex.practicum.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private UUID productId;
    @NotBlank(message = "Имя не должно быть пустым")
    private String productName;
    @NotBlank(message = "Описание не должно быть пустым")
    private String description;
    private String imageSrc;
    @NotNull(message = "Количество не может быть пустым")
    private QuantityState quantityState;
    @NotNull(message = "Состояние товара не может быть пустым")
    private ProductState productState;
    private ProductCategory productCategory;
    @NotNull(message = "Цена не может быть пустой")
    private BigDecimal price;
}
