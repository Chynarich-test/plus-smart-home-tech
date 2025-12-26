package ru.yandex.practicum.model;


import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.dto.product.ProductCategory;
import ru.yandex.practicum.dto.product.ProductState;
import ru.yandex.practicum.dto.product.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "products", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID productId;
    @Column(nullable = false)
    private String productName;
    @Column(nullable = false)
    private String description;
    private String imageSrc;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuantityState quantityState;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductState productState;
    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;
    @Column(nullable = false)
    private BigDecimal price;
}
