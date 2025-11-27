package ru.yandex.practicum.events.sensor.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.events.sensor.dto.SensorEvent;
import ru.yandex.practicum.events.sensor.service.EventSensorService;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventSensorController {
    private final EventSensorService eventSensorService;

    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent event) {
        eventSensorService.collectSensorEvent(event);
    }
}
