package ru.yandex.practicum.events.hub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.events.hub.dto.event.HubEvent;
import ru.yandex.practicum.events.hub.mapper.HubEventMapper;
import ru.yandex.practicum.events.kafka.KafkaEventController;
import ru.yandex.practicum.events.kafka.topic.EventTopics;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Service
@RequiredArgsConstructor
public class EventHubServiceImpl implements EventHubService {
    private final KafkaEventController kafkaEventController;
    private final HubEventMapper hubEventMapper;


    @Override
    public void collectHubEvent(HubEvent event) {
        HubEventAvro hubEventAvro = hubEventMapper.toAvro(event);
        kafkaEventController.produceMassage(EventTopics.TELEMETRY_HUBS, hubEventAvro);
    }
}
