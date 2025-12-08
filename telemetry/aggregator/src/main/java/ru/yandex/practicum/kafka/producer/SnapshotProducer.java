package ru.yandex.practicum.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.config.AvroKafkaClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class SnapshotProducer {
    private final AvroKafkaClient kafkaClient;


    public void produceMassage(String topic, SpecificRecordBase data, String key, long timestamp) {
        Producer<String, SpecificRecordBase> producer = kafkaClient.getProducer();
        ProducerRecord<String, SpecificRecordBase> record =
                new ProducerRecord<>(topic, null, timestamp, key, data);

        producer.send(record, (recordMetadata, e) -> {
            if (e == null) {
                log.debug("Отарпавлненно новое сообщение. \nTopic: {}\nPartition: {}\nOffset: {}\n",
                        recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
            } else {
                log.error("Ошибка во время отправки", e);
            }
        });
    }
}
