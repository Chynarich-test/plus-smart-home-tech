package ru.yandex.practicum.events.sensor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.events.kafka.KafkaEventController;
import ru.yandex.practicum.events.sensor.dto.SensorEvent;
import ru.yandex.practicum.events.sensor.mapper.SensorEventMapper;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Service
@RequiredArgsConstructor
public class EventSensorServiceImpl implements EventSensorService {
    private final KafkaEventController kafkaEventController;
    private final SensorEventMapper sensorEventMapper;
    @Value("${kafka.topic.telemetry.sensors}")
    private String TELEMETRY_SENSORS_TOPIC;

    @Override
    public void collectSensorEvent(SensorEvent event) {
        SensorEventAvro sensorEventAvro = sensorEventMapper.toAvro(event);

        kafkaEventController.produceMassage(TELEMETRY_SENSORS_TOPIC,
                sensorEventAvro,
                event.getHubId(),
                event.getTimestamp().toEpochMilli()
        );
    }
}
