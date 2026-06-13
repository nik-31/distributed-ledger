package com.nikhil.notification_service.config;

import com.nikhil.notification_service.event.MoneyTransferredEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, MoneyTransferredEvent>
    consumerFactory() {

        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        props.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-group-v2");

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JsonDeserializer<MoneyTransferredEvent> deserializer =
                new JsonDeserializer<>(
                        MoneyTransferredEvent.class,
                        false);

        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MoneyTransferredEvent> kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, MoneyTransferredEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                consumerFactory());

        return factory;
    }
}