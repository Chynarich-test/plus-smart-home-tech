package ru.yandex.practicum.model;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Embeddable
@Data
@EqualsAndHashCode
public class ScenarioConditionId implements Serializable {
    private Long scenarioId;
    private String sensorId;
    private Long conditionId;
}
