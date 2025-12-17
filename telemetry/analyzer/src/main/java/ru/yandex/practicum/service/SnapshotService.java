package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dao.ScenarioRepository;
import ru.yandex.practicum.grpc.SnapshotGrpcSender;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.*;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotService {
    private final ScenarioRepository scenarioRepository;
    private final SnapshotGrpcSender snapshotGrpcSender;

    @Transactional(readOnly = true)
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


            Condition condEntity = condition.getCondition();

            int actualValue = getSensorValue(state.getData(), condEntity.getType());
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

    private int getSensorValue(Object sensorData, ConditionType type) {
        if (sensorData instanceof TemperatureSensorAvro temp) {
            return temp.getTemperatureC();
        } else if (sensorData instanceof MotionSensorAvro motion) {
            return motion.getMotion() ? 1 : 0;
        } else if (sensorData instanceof LightSensorAvro light) {
            return light.getLuminosity();
        } else if (sensorData instanceof SwitchSensorAvro sw) {
            return sw.getState() ? 1 : 0;
        } else if (sensorData instanceof ClimateSensorAvro climate) {
            return switch (type) {
                case TEMPERATURE -> climate.getTemperatureC();
                case HUMIDITY -> climate.getHumidity();
                case CO2LEVEL -> climate.getCo2Level();
                default -> throw new IllegalArgumentException("Датчик климата не поддерживает условие: " + type);
            };
        }
        throw new IllegalArgumentException("Неизвестный тип датчика: " + sensorData.getClass());
    }
}

