package ru.yandex.practicum.state;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class StateManager {
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        SensorsSnapshotAvro snapshot = snapshots.getOrDefault(event.getHubId(), new SensorsSnapshotAvro());

        if (snapshot.getHubId() == null) {
            snapshot.setHubId(event.getHubId());
            snapshot.setSensorsState(new HashMap<>());
            snapshot.setTimestamp(event.getTimestamp());
        }

        Map<String, SensorStateAvro> sensorsState = snapshot.getSensorsState();
        String sensorId = event.getId();

        if (sensorsState.containsKey(sensorId)) {
            SensorStateAvro oldState = sensorsState.get(sensorId);

            if (event.getTimestamp().isBefore(oldState.getTimestamp())) {
                return Optional.empty();
            }

            if (oldState.getData().equals(event.getPayload())) {
                return Optional.empty();
            }
        }

        SensorStateAvro newState = new SensorStateAvro();
        newState.setTimestamp(event.getTimestamp());
        newState.setData(event.getPayload());

        sensorsState.put(sensorId, newState);

        snapshot.setTimestamp(event.getTimestamp());
        snapshots.put(event.getHubId(), snapshot);
        return Optional.of(snapshot);
    }
}
