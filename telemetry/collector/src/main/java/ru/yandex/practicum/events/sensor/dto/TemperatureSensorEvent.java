package ru.yandex.practicum.events.sensor.dto;

import jakarta.validation.constraints.NotNull;

public class TemperatureSensorEvent extends SensorEvent {
    @NotNull
    private Integer temperatureC;
    @NotNull
    private Integer temperatureF;


    @Override
    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}
