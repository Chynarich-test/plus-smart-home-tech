package ru.yandex.practicum.kafka.config;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.yandex.practicum.avro.deserializer.SensorEventDeserializer;
import ru.yandex.practicum.avro.serializer.GeneralAvroSerializer;

import java.util.Properties;

@Configuration
public class AvroKafkaConfiguration {
    @Bean
    @Scope("prototype")
    public AvroKafkaClient getClient(
            @Value("${kafka.server}") String kafkaServer,
            @Value("${kafka.producer.id}") String kafkaProducerId,
            @Value("${kafka.consumer.id}") String kafkaConsumerId) {
        return new AvroKafkaClient() {
            private Consumer<String, SpecificRecordBase> consumer;

            private Producer<String, SpecificRecordBase> producer;

            @Override
            public Consumer<String, SpecificRecordBase> getConsumer() {
                if (consumer == null) {
                    initConsumer();
                }
                return consumer;
            }

            private void initConsumer() {
                Properties config = new Properties();
                config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
                config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
                config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorEventDeserializer.class);
                config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerId);
                consumer = new KafkaConsumer<>(config);
            }


            @Override
            public Producer<String, SpecificRecordBase> getProducer() {
                if (producer == null) {
                    initProducer();
                }
                return producer;
            }

            private void initProducer() {
                Properties config = new Properties();
                config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
                config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
                config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class);
                config.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaProducerId);

                producer = new KafkaProducer<>(config);
            }

            @Override
            public void stop() {
                if (consumer != null) {
                    consumer.close();
                }
                if (producer != null) {
                    producer.close();
                }
            }
        };
    }

}
