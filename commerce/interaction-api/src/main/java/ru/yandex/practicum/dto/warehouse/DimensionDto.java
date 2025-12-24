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
public class DimensionDto {
    @NotNull(message = "Ширина не может быть пустой")
    private Double width;
    @NotNull(message = "Высота не может быть пустой")
    private Double height;
    @NotNull(message = "Глубина не может быть пустой")
    private Double depth;
}
