package ru.yandex.practicum.dispatcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.service.HubService;

@Component
@RequiredArgsConstructor
@Slf4j
public class HubEventDispatcher {
    private final HubService hubService;

    public void processPayload(HubEventAvro event) {
        String hubId = event.getHubId();
        Object payload = event.getPayload();

//        думал сделать через map struct как в прошлом спринте, также перенеся туда логику сервиса по маппингу,
//        но не потянул, пришлось делать вот такой некрасивой башней из if
        if (payload instanceof DeviceAddedEventAvro deviceAdded) {
            hubService.addDevice(deviceAdded, hubId);
        } else if (payload instanceof DeviceRemovedEventAvro deviceRemoved) {
            hubService.removeDevice(deviceRemoved, hubId);
        } else if (payload instanceof ScenarioAddedEventAvro scenarioAdded) {
            hubService.addScenario(scenarioAdded, hubId);
        } else if (payload instanceof ScenarioRemovedEventAvro scenarioRemoved) {
            hubService.removeScenario(scenarioRemoved, hubId);
        } else {
            log.warn("Неизвестный тип пейлоада: {}", payload.getClass().getName());
        }
    }
}
