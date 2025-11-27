package ru.yandex.practicum.events.hub.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.events.hub.dto.event.HubEvent;
import ru.yandex.practicum.events.hub.service.EventHubService;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventHubController {
    private final EventHubService eventHubService;

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent event) {
        eventHubService.collectHubEvent(event);
    }
}
