package ru.otus.hw;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import static ru.otus.hw.Helper.TOPIC_1;
import static ru.otus.hw.Helper.TOPIC_2;

public class Producer {
    public static void main(String[] args) throws InterruptedException {
        try (KafkaProducer<String, String> kafkaProducer =
                     new KafkaProducer<>(Helper.createProducerConfig(p -> p.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "hw3")))) {
            kafkaProducer.initTransactions();
            kafkaProducer.beginTransaction();
            sendMessages(kafkaProducer, TOPIC_1);
            sendMessages(kafkaProducer, TOPIC_2);
            kafkaProducer.commitTransaction();
            kafkaProducer.beginTransaction();
            kafkaProducer.send(new ProducerRecord<>(TOPIC_1, "Message #1"));
            kafkaProducer.send(new ProducerRecord<>(TOPIC_1, "Message #2"));
            kafkaProducer.send(new ProducerRecord<>(TOPIC_2, "Message #1"));
            kafkaProducer.send(new ProducerRecord<>(TOPIC_2, "Message #2"));
            Thread.sleep(5000);
            kafkaProducer.abortTransaction();
            Thread.sleep(5000);
        }
    }

    private static void sendMessages(KafkaProducer<String, String> kafkaProducer, String topic) {
        for(int i = 0; i < 5; i++) {
            kafkaProducer.send(new ProducerRecord<>(topic, "Message #" + (i+1)));
        }
    }
}
