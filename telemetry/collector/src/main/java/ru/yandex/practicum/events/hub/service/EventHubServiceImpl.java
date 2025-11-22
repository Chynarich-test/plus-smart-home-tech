package ru.yandex.practicum.events.hub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.events.hub.dto.event.HubEvent;
import ru.yandex.practicum.events.hub.mapper.HubEventMapper;
import ru.yandex.practicum.events.kafka.KafkaEventController;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Service
@RequiredArgsConstructor
public class EventHubServiceImpl implements EventHubService {
    private final KafkaEventController kafkaEventController;
    private final HubEventMapper hubEventMapper;
    @Value("${kafka.topic.telemetry.hubs}")
    private String TELEMETRY_HUBS_TOPIC;

    @Override
    public void collectHubEvent(HubEvent event) {
        HubEventAvro hubEventAvro = hubEventMapper.toAvro(event);
        kafkaEventController.produceMassage(TELEMETRY_HUBS_TOPIC,
                hubEventAvro,
                event.getHubId(),
                event.getTimestamp().toEpochMilli()
        );
    }
}
