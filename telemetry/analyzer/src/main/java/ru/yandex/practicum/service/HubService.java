package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dao.ScenarioRepository;
import ru.yandex.practicum.dao.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.*;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HubService {
    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;

    @Transactional
    public void addDevice(DeviceAddedEventAvro event, String hubId) {
        if (sensorRepository.findById(event.getId()).isPresent()) {
            return;
        }

        Sensor sensor = new Sensor();
        sensor.setId(event.getId());
        sensor.setHubId(hubId);

        sensorRepository.save(sensor);
    }

    @Transactional
    public void removeDevice(DeviceRemovedEventAvro event, String hubId) {
        sensorRepository.findById(event.getId()).ifPresent(sensor -> {
            if (sensor.getHubId().equals(hubId)) {
                sensorRepository.delete(sensor);
            }
        });
    }

    @Transactional
    public void addScenario(ScenarioAddedEventAvro event, String hubId) {
        scenarioRepository.findByHubIdAndName(hubId, event.getName())
                .ifPresent(scenario -> {
                    scenarioRepository.delete(scenario);
                    scenarioRepository.flush();
                });

        Scenario scenario = new Scenario();
        scenario.setHubId(hubId);
        scenario.setName(event.getName());

        List<ScenarioCondition> conditions = new ArrayList<>();

        for (ScenarioConditionAvro condAvro : event.getConditions()) {
            ScenarioCondition sc = new ScenarioCondition();
            sc.setScenario(scenario);

            Sensor sensor = sensorRepository.getReferenceById(condAvro.getSensorId());
            sc.setSensor(sensor);

            Condition condition = new Condition();
            condition.setType(ConditionType.valueOf(condAvro.getType().name()));
            condition.setOperation(ConditionOperation.valueOf(condAvro.getOperation().name()));

            condition.setValue(extractValue(condAvro.getValue()));

            sc.setCondition(condition);
            conditions.add(sc);
        }

        scenario.setConditions(conditions);

        List<ScenarioAction> actions = new ArrayList<>();
        for (DeviceActionAvro actionAvro : event.getActions()) {
            ScenarioAction sa = new ScenarioAction();
            sa.setScenario(scenario);

            Sensor sensor = sensorRepository.getReferenceById(actionAvro.getSensorId());
            sa.setSensor(sensor);

            Action action = new Action();
            action.setType(ActionType.valueOf(actionAvro.getType().name()));
            action.setValue(actionAvro.getValue());

            sa.setAction(action);
            actions.add(sa);
        }
        scenario.setActions(actions);

        scenarioRepository.save(scenario);
    }

    @Transactional
    public void removeScenario(ScenarioRemovedEventAvro event, String hubId) {
        scenarioRepository.findByHubIdAndName(hubId, event.getName())
                .ifPresent(scenarioRepository::delete);
    }

    private Integer extractValue(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        }
        return null;
    }

}
