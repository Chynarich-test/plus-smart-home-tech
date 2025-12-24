package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.model.Stock;
import ru.yandex.practicum.repository.ProductRepository;
import ru.yandex.practicum.repository.StockRepository;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseService {
    private static final String[] ADDRESSES =
            new String[]{"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final WarehouseMapper warehouseMapper;

    private boolean isExistsProduct(UUID id) {
        return productRepository.existsById(id);
    }

    @Transactional
    public void addNewProduct(NewProductInWarehouseRequest dto) {
        if (isExistsProduct(dto.getProductId())) throw new SpecifiedProductAlreadyInWarehouseException(
                "Товар с таким описанием уже зарегистрирован на складе");

        Product newProduct = warehouseMapper.toProduct(dto);

        newProduct = productRepository.save(newProduct);

        Stock newStock = Stock.builder()
                .product(newProduct)
                .quantity(0L)
                .build();

        stockRepository.save(newStock);
    }

    public BookedProductsDto checkProductsNumber(ShoppingCartDto dto) {
        Map<UUID, Long> products = dto.getProducts();

        List<Stock> stocks = stockRepository.findAllByProductIdIn(products.keySet());

        if (stocks.size() < products.size())
            throw new ProductInShoppingCartLowQuantityInWarehouse("Недостаточно товара на складе");

        BookedProductsDto bookedProductsDto = new BookedProductsDto();

        for (Stock stock : stocks) {
            Long requestedQuantity = products.get(stock.getProductId());
            if (requestedQuantity > stock.getQuantity()) {
                throw new ProductInShoppingCartLowQuantityInWarehouse("Недостаточно товара на складе");
            }
            bookedProductsDto.setDeliveryVolume(
                    bookedProductsDto.getDeliveryVolume()
                            + getVolumeProduct(stock.getProduct()) * requestedQuantity
            );
            bookedProductsDto.setDeliveryWeight(
                    bookedProductsDto.getDeliveryWeight() +
                            stock.getProduct().getWeight() * requestedQuantity
            );
            if (stock.getProduct().getFragile()) bookedProductsDto.setFragile(true);
        }

        return bookedProductsDto;
    }

    private Double getVolumeProduct(Product product) {
        return product.getDepth() * product.getHeight() * product.getWidth();
    }


    public void takeProducts(AddProductToWarehouseRequest dto) {
        Stock stock = stockRepository.findById(dto.getProductId()).orElseThrow(() ->
                new NoSpecifiedProductInWarehouseException("Нет информации о товаре на складе"));

        stock.setQuantity(stock.getQuantity() + dto.getQuantity());

        stockRepository.save(stock);
    }

    public AddressDto getAddress() {
        return AddressDto.builder()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .build();
    }
}
