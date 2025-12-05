package ru.yandex.practicum.events.hub.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;
import ru.yandex.practicum.events.hub.dto.ActionType;
import ru.yandex.practicum.events.hub.dto.OperationType;
import ru.yandex.practicum.events.hub.dto.ScenarioType;
import ru.yandex.practicum.events.hub.dto.event.HubEvent;
import ru.yandex.practicum.events.hub.dto.event.*;
import ru.yandex.practicum.grpc.telemetry.event.*;

@Mapper(componentModel = "spring")
public interface HubProtoMapper {
    default HubEvent toDto(HubEventProto proto) {
        return switch (proto.getPayloadCase()) {
            case DEVICE_ADDED -> toDeviceAddedDto(proto);
            case DEVICE_REMOVED -> toDeviceRemovedDto(proto);
            case SCENARIO_ADDED -> toScenarioAdded(proto);
            case SCENARIO_REMOVED -> toScenarioRemoved(proto);
            case PAYLOAD_NOT_SET -> throw new IllegalArgumentException("Неизвестный тип");
        };
    }

    default java.time.Instant mapTime(com.google.protobuf.Timestamp timestamp) {
        return java.time.Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }

    @Mapping(target = "id", source = "deviceAdded.id")
    @Mapping(target = "deviceType", source = "deviceAdded.type")
    DeviceAddedEvent toDeviceAddedDto(HubEventProto proto);

    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.NULL)
    DeviceType mapDeviceType(DeviceTypeProto type);

    @Mapping(target = "id", source = "deviceRemoved.id")
    DeviceRemovedEvent toDeviceRemovedDto(HubEventProto proto);

    @Mapping(target = "name", source = "scenarioAdded.name")
    @Mapping(target = "conditions", source = "scenarioAdded.conditionList")
    @Mapping(target = "actions", source = "scenarioAdded.actionList")
    ScenarioAddedEvent toScenarioAdded(HubEventProto proto);

    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.NULL)
    ScenarioType mapConditionType(ConditionTypeProto type);

    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.NULL)
    OperationType mapOperationType(ConditionOperationProto type);

    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.NULL)
    ActionType mapActionType(ActionTypeProto type);

    @Mapping(target = "name", source = "scenarioRemoved.name")
    ScenarioRemovedEvent toScenarioRemoved(HubEventProto proto);
}
