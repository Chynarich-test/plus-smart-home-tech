package ru.yandex.practicum.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ScenarioActionId implements Serializable {
    private Long scenario;
    private String sensor;
    private Long action;
}
