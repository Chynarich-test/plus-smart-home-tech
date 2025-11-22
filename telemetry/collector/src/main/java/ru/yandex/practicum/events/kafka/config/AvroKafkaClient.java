package ru.yandex.practicum.events.kafka.config;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;

public interface AvroKafkaClient {
    Producer<String, SpecificRecordBase> getProducer();

    void stop();
}
