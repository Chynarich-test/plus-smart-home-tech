package ru.yandex.practicum.client;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;

public interface WarehouseOperations {
    @PostMapping("/check")
    BookedProductsDto checkProductsNumber(@Valid @RequestBody ShoppingCartDto dto);
}
