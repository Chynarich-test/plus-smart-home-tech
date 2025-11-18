package ru.yandex.practicum.events.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.events.kafka.config.AvroKafkaClient;

@Component
@RequiredArgsConstructor
public class KafkaEventController {
    private final AvroKafkaClient kafkaClient;

    public void produceMassage(String topic, SpecificRecordBase data) {
        Producer<String, SpecificRecordBase> producer = kafkaClient.getProducer();
        producer.send(new ProducerRecord<>(topic, data));
        producer.flush();
    }
}
