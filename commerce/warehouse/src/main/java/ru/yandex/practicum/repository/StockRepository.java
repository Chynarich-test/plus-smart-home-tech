package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.Stock;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface StockRepository extends JpaRepository<Stock, UUID> {

    @EntityGraph(attributePaths = "product")
    List<Stock> findAllByProductIdIn(Set<UUID> ids);
}
