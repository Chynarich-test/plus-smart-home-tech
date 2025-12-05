package ru.yandex.practicum.events.сontroller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.events.hub.dto.event.HubEvent;
import ru.yandex.practicum.events.hub.mapper.HubProtoMapper;
import ru.yandex.practicum.events.hub.service.EventHubService;
import ru.yandex.practicum.events.sensor.dto.SensorEvent;
import ru.yandex.practicum.events.sensor.mapper.SensorProtoMapper;
import ru.yandex.practicum.events.sensor.service.EventSensorService;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@GrpcService
@RequiredArgsConstructor
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {
    private final EventHubService eventHubService;
    private final HubProtoMapper hubProtoMapper;
    private final EventSensorService eventSensorService;
    private final SensorProtoMapper sensorProtoMapper;

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            HubEvent eventDto = hubProtoMapper.toDto(request);

            eventHubService.collectHubEvent(eventDto);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            SensorEvent eventDto = sensorProtoMapper.toDto(request);

            eventSensorService.collectSensorEvent(eventDto);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }
}
