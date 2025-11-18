package ru.yandex.practicum.events.hub.service;

import jakarta.validation.Valid;
import ru.yandex.practicum.events.hub.dto.event.HubEvent;

public interface EventHubService {
    void collectHubEvent(@Valid HubEvent event);
}
