package ru.yandex.practicum.kafka.config;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;

public interface AvroKafkaClient {
    Consumer<String, SpecificRecordBase> getConsumer();

    void stop();
}
