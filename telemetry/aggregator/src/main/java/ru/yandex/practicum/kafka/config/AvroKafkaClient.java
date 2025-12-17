package ru.yandex.practicum.kafka.config;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;

public interface AvroKafkaClient {
    Consumer<String, SpecificRecordBase> getConsumer();

    Producer<String, SpecificRecordBase> getProducer();

    void stop();
}
