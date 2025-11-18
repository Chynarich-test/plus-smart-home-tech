package ru.yandex.practicum.events.hub.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ScenarioCondition {
    private String sensorId;
    private ScenarioType type;
    private OperationType operation;
    private Integer value;
}
