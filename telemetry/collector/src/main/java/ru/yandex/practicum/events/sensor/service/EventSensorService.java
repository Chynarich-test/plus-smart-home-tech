package ru.yandex.practicum.events.sensor.service;

import jakarta.validation.Valid;
import ru.yandex.practicum.events.sensor.dto.SensorEvent;

public interface EventSensorService {
    void collectSensorEvent(@Valid SensorEvent event);
}
