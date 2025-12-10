package ru.yandex.practicum.grpc;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.ScenarioAction;

import java.time.Instant;

@Slf4j
@Component
public class SnapshotGrpcSender {
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public SnapshotGrpcSender(@GrpcClient("hub-router") HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient) {
        this.hubRouterClient = hubRouterClient;
    }

    public void executeActions(Scenario scenario, String hubId) {
        for (ScenarioAction action : scenario.getActions()) {
            Instant now = Instant.now();

            Timestamp protoTimestamp = Timestamp.newBuilder()
                    .setSeconds(now.getEpochSecond())
                    .setNanos(now.getNano())
                    .build();

            DeviceActionRequest request = DeviceActionRequest.newBuilder()
                    .setHubId(hubId)
                    .setScenarioName(scenario.getName())
                    .setAction(DeviceActionProto.newBuilder()
                            .setSensorId(action.getSensor().getId())
                            .setType(ActionTypeProto.valueOf(action.getAction().getType().name()))
                            .setValue(action.getAction().getValue())
                            .build())
                    .setTimestamp(protoTimestamp)
                    .build();

            try {
                hubRouterClient.handleDeviceAction(request);
                log.info("Действие отправлено: hub={}, sensor={}, type={}",
                        hubId, action.getSensor().getId(), action.getAction().getType());
            } catch (Exception e) {
                log.error("Не удалось отправить действие в HubRouter", e);
            }
        }
    }
}
