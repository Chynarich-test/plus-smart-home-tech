package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dao.ScenarioRepository;
import ru.yandex.practicum.grpc.SnapshotGrpcSender;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.ConditionOperation;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.ScenarioCondition;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotService {
    private final ScenarioRepository scenarioRepository;
    private final SnapshotGrpcSender snapshotGrpcSender;

    public void checkSnapshot(SensorsSnapshotAvro snapshot) {
        List<Scenario> scenarios = scenarioRepository.findByHubId(snapshot.getHubId());

        for (Scenario scenario : scenarios) {
            try {
                if (checkConditions(scenario.getConditions(), snapshot)) {
                    snapshotGrpcSender.executeActions(scenario, snapshot.getHubId());
                }
            } catch (Exception e) {
                log.error("Ошибка при обработке сценария: {}", scenario.getName(), e);
            }
        }
    }

    private boolean checkConditions(List<ScenarioCondition> conditions, SensorsSnapshotAvro snapshot) {
        for (ScenarioCondition condition : conditions) {
            String sensorId = condition.getSensor().getId();

            SensorStateAvro state = snapshot.getSensorsState().get(sensorId);

            if (state == null) {
                return false;
            }

            int actualValue = getSensorValue(state.getData());

            Condition condEntity = condition.getCondition();
            int expectedValue = condEntity.getValue();
            ConditionOperation operation = condEntity.getOperation();

            if (!compare(actualValue, operation, expectedValue)) {
                return false;
            }
        }
        return true;
    }

    private boolean compare(int actual, ConditionOperation operation, int expected) {
        return switch (operation) {
            case EQUALS -> actual == expected;
            case GREATER_THAN -> actual > expected;
            case LOWER_THAN -> actual < expected;
        };
    }

    private int getSensorValue(Object sensorData) {
        if (sensorData instanceof TemperatureSensorAvro temp) {
            return temp.getTemperatureC();
        } else if (sensorData instanceof MotionSensorAvro motion) {
            return motion.getMotion() ? 1 : 0;
        } else if (sensorData instanceof LightSensorAvro light) {
            return light.getLuminosity();
        } else if (sensorData instanceof SwitchSensorAvro sw) {
            return sw.getState() ? 1 : 0;
        } else if (sensorData instanceof ClimateSensorAvro climate) {
            return climate.getCo2Level();
        }
        throw new IllegalArgumentException("Неизвестный тип датчика: " + sensorData.getClass());
    }
}

