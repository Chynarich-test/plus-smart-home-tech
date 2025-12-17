package ru.yandex.practicum.events.sensor.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.events.sensor.dto.*;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Mapper(componentModel = "spring")
public interface SensorProtoMapper {
    default SensorEvent toDto(SensorEventProto proto) {
        return switch (proto.getPayloadCase()) {
            case MOTION_SENSOR -> toMotionDto(proto);
            case TEMPERATURE_SENSOR -> toTemperatureDto(proto);
            case LIGHT_SENSOR -> toLightDto(proto);
            case CLIMATE_SENSOR -> toClimateDto(proto);
            case SWITCH_SENSOR -> toSwitchDto(proto);
            case PAYLOAD_NOT_SET -> throw new IllegalArgumentException("Неизвестный тип");
        };
    }

    default java.time.Instant mapTime(com.google.protobuf.Timestamp timestamp) {
        return java.time.Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }

    @Mapping(target = "motion", source = "motionSensor.motion")
    @Mapping(target = "linkQuality", source = "motionSensor.linkQuality")
    @Mapping(target = "voltage", source = "motionSensor.voltage")
    MotionSensorEvent toMotionDto(SensorEventProto proto);

    TemperatureSensorEvent toTemperatureDto(SensorEventProto proto);

    @Mapping(target = "linkQuality", source = "lightSensor.linkQuality")
    @Mapping(target = "luminosity", source = "lightSensor.luminosity")
    LightSensorEvent toLightDto(SensorEventProto proto);

    @Mapping(target = "temperatureC", source = "climateSensor.temperatureC")
    @Mapping(target = "humidity", source = "climateSensor.humidity")
    @Mapping(target = "co2Level", source = "climateSensor.co2Level")
    ClimateSensorEvent toClimateDto(SensorEventProto proto);

    @Mapping(target = "state", source = "switchSensor.state")
    SwitchSensorEvent toSwitchDto(SensorEventProto proto);
}
