package ru.yandex.practicum.kafka.config;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.avro.deserializer.HubEventDeserializer;
import ru.yandex.practicum.avro.deserializer.SensorsSnapshotDeserializer;

import java.util.Properties;

@Configuration
public class AvroKafkaConfiguration {
    @Value("${kafka.server}")
    private String kafkaServer;

    @Value("${kafka.consumer.id}")
    private String kafkaConsumerId;

    @Bean("hubEventClient")
    public AvroKafkaClient hubEventClient() {
        return new AvroKafkaClient() {
            private Consumer<String, SpecificRecordBase> consumer;

            @Override
            public Consumer<String, SpecificRecordBase> getConsumer() {
                if (consumer == null) {
                    Properties config = new Properties();
                    config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
                    config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
                    config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, HubEventDeserializer.class);
                    config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerId);
                    consumer = new KafkaConsumer<>(config);
                }
                return consumer;
            }

            @Override
            public void stop() {
                if (consumer != null) consumer.close();
            }
        };
    }

    @Bean("snapshotClient")
    public AvroKafkaClient snapshotClient() {
        return new AvroKafkaClient() {
            private Consumer<String, SpecificRecordBase> consumer;

            @Override
            public Consumer<String, SpecificRecordBase> getConsumer() {
                if (consumer == null) {
                    Properties config = new Properties();
                    config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
                    config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
                    config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorsSnapshotDeserializer.class);
                    config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerId);
                    consumer = new KafkaConsumer<>(config);
                }
                return consumer;
            }

            @Override
            public void stop() {
                if (consumer != null) consumer.close();
            }
        };
    }

}
