package ru.yandex.practicum.events.hub.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.SubclassMapping;
import ru.yandex.practicum.events.hub.dto.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Mapper(componentModel = "spring")
public interface HubEventMapper {

    @SubclassMapping(source = DeviceAddedEvent.class, target = HubEventAvro.class)
    @SubclassMapping(source = DeviceRemovedEvent.class, target = HubEventAvro.class)
    @SubclassMapping(source = ScenarioAddedEvent.class, target = HubEventAvro.class)
    @SubclassMapping(source = ScenarioRemovedEvent.class, target = HubEventAvro.class)
    HubEventAvro toAvro(HubEvent event);

    @Named("deviceAddedToPayload")
    @Mapping(target = "type", source = "deviceType")
    DeviceAddedEventAvro toDeviceAddedPayload(DeviceAddedEvent dto);

    @Named("deviceRemovedToPayload")
    DeviceRemovedEventAvro toDeviceRemovedPayload(DeviceRemovedEvent dto);

    @Named("scenarioAddedToPayload")
    ScenarioAddedEventAvro toScenarioAddedPayload(ScenarioAddedEvent dto);

    @Named("scenarioRemovedToPayload")
    ScenarioRemovedEventAvro toScenarioRemovedPayload(ScenarioRemovedEvent dto);

    @Mapping(target = "payload", source = "dto", qualifiedByName = "deviceAddedToPayload")
    HubEventAvro toAvro(DeviceAddedEvent dto);

    @Mapping(target = "payload", source = "dto", qualifiedByName = "deviceRemovedToPayload")
    HubEventAvro toAvro(DeviceRemovedEvent dto);

    @Mapping(target = "payload", source = "dto", qualifiedByName = "scenarioAddedToPayload")
    HubEventAvro toAvro(ScenarioAddedEvent dto);

    @Mapping(target = "payload", source = "dto", qualifiedByName = "scenarioRemovedToPayload")
    HubEventAvro toAvro(ScenarioRemovedEvent dto);

}
