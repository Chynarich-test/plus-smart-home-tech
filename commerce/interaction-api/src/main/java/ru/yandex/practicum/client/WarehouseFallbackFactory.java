package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.exception.ServiceUnavailableException;

@Component
public class WarehouseFallbackFactory implements FallbackFactory<WarehouseClient> {

    @Override
    public WarehouseClient create(Throwable cause) {
        return new WarehouseClient() {

            @Override
            public BookedProductsDto checkProductsNumber(ShoppingCartDto dto) throws ProductInShoppingCartLowQuantityInWarehouse {
                if (cause instanceof ProductInShoppingCartLowQuantityInWarehouse ||
                        cause instanceof ProductNotFoundException) {
                    throw (RuntimeException) cause;
                }

                throw new ServiceUnavailableException("Сервис склада временно недоступен. Мы уже чиним!");
            }
        };
    }
}
