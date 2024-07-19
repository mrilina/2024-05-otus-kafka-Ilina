package ru.otus.hw;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static ru.otus.hw.Helper.NON_TRANSACTIONAL_CONSUMER;
import static ru.otus.hw.Helper.TOPIC_1;
import static ru.otus.hw.Helper.TOPIC_2;
import static ru.otus.hw.Helper.TRANSACTIONAL_CONSUMER;

public class Consumer {
    public static void main(String[] args) {
        List<String> topics = Arrays.asList(TOPIC_1, TOPIC_2);
        ConsumerRecords<String, String> records;
        try (KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(Helper.createConsumerConfig(p -> {
            p.put(ConsumerConfig.GROUP_ID_CONFIG, "non-transactional-group");
            p.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_uncommitted");
        }))) {
            System.out.println(NON_TRANSACTIONAL_CONSUMER + "\n");
            kafkaConsumer.subscribe(topics);
            while (!(records = kafkaConsumer.poll(Duration.ofSeconds(10))).isEmpty()) {
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println("%s: %s".formatted(record.topic(), record.value()));
                }
            }
        }

        try (KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(Helper.createConsumerConfig(p -> {
            p.put(ConsumerConfig.GROUP_ID_CONFIG, "transactional-group");
            p.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        }))) {
            System.out.println(TRANSACTIONAL_CONSUMER + "\n");
            kafkaConsumer.subscribe(topics);
            while (!(records = kafkaConsumer.poll(Duration.ofSeconds(10))).isEmpty()) {
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println("%s: %s".formatted(record.topic(), record.value()));
                }
            }
        }
    }
}
