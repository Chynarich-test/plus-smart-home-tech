package ru.yandex.practicum.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse", fallbackFactory = WarehouseFallbackFactory.class)
public interface WarehouseClient {
    @PostMapping("/check")
    BookedProductsDto checkProductsNumber(@Valid @RequestBody ShoppingCartDto dto)
            throws ProductInShoppingCartLowQuantityInWarehouse;
}
