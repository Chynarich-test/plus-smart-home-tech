package ru.yandex.practicum.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.Condition;

public interface ConditionRepository extends JpaRepository<Condition, Long> {
}