package ru.yandex.practicum.kafka.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.config.AvroKafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.service.SnapshotService;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SnapshotProcessor implements Runnable {

    private final Duration CONSUME_ATTEMPT_TIMEOUT;
    private final AvroKafkaClient kafkaClient;
    private final String snapshotTopic;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private final SnapshotService snapshotService;

    public SnapshotProcessor(@Value("${kafka.snapshots.consume.attempt.timeout.ms}") Duration consumeAttemptTimeout,
                             @Qualifier("snapshotClient") AvroKafkaClient kafkaClient,
                             @Value("${kafka.topic.analyzer.snapshots}") String snapshotTopic, SnapshotService snapshotService) {
        CONSUME_ATTEMPT_TIMEOUT = consumeAttemptTimeout;
        this.kafkaClient = kafkaClient;
        this.snapshotTopic = snapshotTopic;
        this.snapshotService = snapshotService;
    }

    @Override
    public void run() {
        Consumer<String, SpecificRecordBase> consumer = kafkaClient.getConsumer();

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(List.of(snapshotTopic));
            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    if (record.value() instanceof SensorsSnapshotAvro snapshot) {
                        try {
                            snapshotService.checkSnapshot(snapshot);
                        } catch (Exception e) {
                            log.error("Ошибка обработки события: {}", snapshot, e);
                        }
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
