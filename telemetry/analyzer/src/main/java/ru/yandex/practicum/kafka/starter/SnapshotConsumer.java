package ru.yandex.practicum.kafka.starter;

import lombok.AllArgsConstructor;
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

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class SnapshotConsumer {

    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);
    private final AvroKafkaClient kafkaClient;
    @Value("${kafka.topic.analyzer.snapshots}")
    private final String snapshotTopic;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();


    public void start() {
        Consumer<String, SpecificRecordBase> consumer = kafkaClient.getConsumer();

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(List.of(snapshotTopic));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {


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
