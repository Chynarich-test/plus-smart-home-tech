package ru.yandex.practicum.starter;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.config.AvroKafkaClient;
import ru.yandex.practicum.kafka.producer.SnapshotProducer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.state.StateManager;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class AggregationStarter {

    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);
    private final AvroKafkaClient kafkaClient;
    private final String sensorsTopic;
    private final String snapshotTopic;
    private final SnapshotProducer snapshotProducer;
    private final StateManager stateManager;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    public AggregationStarter(AvroKafkaClient kafkaClient,
                              @Value("${kafka.topic.telemetry.sensors}") String sensorsTopic,
                              @Value("${kafka.topic.telemetry.snapshots}") String snapshotTopic,
                              SnapshotProducer snapshotProducer, StateManager stateManager) {
        this.kafkaClient = kafkaClient;
        this.sensorsTopic = sensorsTopic;
        this.snapshotTopic = snapshotTopic;
        this.snapshotProducer = snapshotProducer;
        this.stateManager = stateManager;
    }

    public void start() {
        Consumer<String, SpecificRecordBase> consumer = kafkaClient.getConsumer();

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(List.of(sensorsTopic));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    if (record.value() instanceof SensorEventAvro event) {
                        try {
                            Optional<SensorsSnapshotAvro> newSnapshot = stateManager.updateState(event);

                            newSnapshot.ifPresent(sensorsSnapshotAvro ->
                                    snapshotProducer.produceMassage(snapshotTopic, sensorsSnapshotAvro,
                                            sensorsSnapshotAvro.getHubId(),
                                            sensorsSnapshotAvro.getTimestamp().toEpochMilli()));
                        } catch (Exception e) {
                            log.error("Ошибка обработки события: {}", event, e);
                        }
                    } else {
                        log.warn("Получено неизвестное сообщение: {}", record.value());
                    }

                    currentOffsets.put(
                            new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset() + 1)
                    );
                }

                if (!records.isEmpty()) {
                    consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                        if (exception != null) {
                            log.warn("Ошибка фиксации оффсетов: {}", offsets, exception);
                        }
                    });
                }

            }

        } catch (WakeupException ignored) {
            log.info("Получен сигнал завершения работы");
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {

            try {
                consumer.commitSync(currentOffsets);
            } finally {
                kafkaClient.stop();
            }
        }
    }
}
