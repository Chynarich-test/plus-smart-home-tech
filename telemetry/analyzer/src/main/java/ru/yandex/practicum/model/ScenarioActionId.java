package ru.yandex.practicum.model;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@Embeddable
public class ScenarioActionId {
    private Long scenarioId;
    private String sensorId;
    private Long actionId;
}
