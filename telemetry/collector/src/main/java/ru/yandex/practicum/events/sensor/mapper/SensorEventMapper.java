package ru.yandex.practicum.events.sensor.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.SubclassMapping;
import ru.yandex.practicum.events.sensor.dto.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Mapper(componentModel = "spring")
public interface SensorEventMapper {

    @SubclassMapping(source = ClimateSensorEvent.class, target = SensorEventAvro.class)
    @SubclassMapping(source = LightSensorEvent.class, target = SensorEventAvro.class)
    @SubclassMapping(source = MotionSensorEvent.class, target = SensorEventAvro.class)
    @SubclassMapping(source = SwitchSensorEvent.class, target = SensorEventAvro.class)
    @SubclassMapping(source = TemperatureSensorEvent.class, target = SensorEventAvro.class)
    SensorEventAvro toAvro(SensorEvent event);

    @Named("climateToPayload")
    ClimateSensorAvro toClimatePayload(ClimateSensorEvent dto);

    @Named("lightToPayload")
    LightSensorAvro toLightPayload(LightSensorEvent dto);

    @Named("motionToPayload")
    MotionSensorAvro toMotionPayload(MotionSensorEvent dto);

    @Named("switchToPayload")
    SwitchSensorAvro toSwitchPayload(SwitchSensorEvent dto);

    @Named("temperatureToPayload")
    TemperatureSensorAvro toTemperaturePayload(TemperatureSensorEvent dto);

    @Mapping(target = "payload", source = "dto", qualifiedByName = "climateToPayload")
    SensorEventAvro toAvro(ClimateSensorEvent dto);

    @Mapping(target = "payload", source = "dto", qualifiedByName = "lightToPayload")
    SensorEventAvro toAvro(LightSensorEvent dto);

    @Mapping(target = "payload", source = "dto", qualifiedByName = "motionToPayload")
    SensorEventAvro toAvro(MotionSensorEvent dto);

    @Mapping(target = "payload", source = "dto", qualifiedByName = "switchToPayload")
    SensorEventAvro toAvro(SwitchSensorEvent dto);

    @Mapping(target = "payload", source = "dto", qualifiedByName = "temperatureToPayload")
    SensorEventAvro toAvro(TemperatureSensorEvent dto);
}
