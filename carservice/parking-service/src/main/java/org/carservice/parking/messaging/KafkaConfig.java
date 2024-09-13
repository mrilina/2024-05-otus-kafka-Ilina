package org.carservice.parking.messaging;

import lombok.Setter;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@EnableKafka
class KafkaConfig {

    @Bean
    @ConfigurationProperties(prefix = "kafka.topic.spacebooking.inbox.events")
    KafkaTopic spaceBookingInboxTopicProps() {
        return new KafkaTopic();
    }

    @Bean
    @ConfigurationProperties(prefix = "kafka.topic.spacebooking.outbox.events")
    KafkaTopic spaceBookingOutboxTopicProps() {
        return new KafkaTopic();
    }

    /**
     * space-booking inbox topic creation
     */
    @Bean
    NewTopic spaceBookingInboxTopic() {
        var props = spaceBookingInboxTopicProps();
        return new NewTopic(props.name, props.partitions, props.replicas);
    }

    /**
     * space-booking outbox topic creation
     */
    @Bean
    NewTopic spaceBookingOutboxTopic() {
        var props = spaceBookingOutboxTopicProps();
        return new NewTopic(props.name, props.partitions, props.replicas);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, SpaceBookingEventPayload> spaceBookingKLCFactory(KafkaProperties props) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, SpaceBookingEventPayload>();
        factory.setConsumerFactory(consumerFactory(props));
        factory.setConcurrency(props.getListener().getConcurrency());
        return factory;
    }

    @Bean
    ConsumerFactory<String, SpaceBookingEventPayload> consumerFactory(KafkaProperties props) {
        return new DefaultKafkaConsumerFactory<>(
                props.buildConsumerProperties(null),
                new StringDeserializer(),
                new JsonDeserializer<>(SpaceBookingEventPayload.class)
        );
    }

    @Setter
    private static class KafkaTopic {
        private String name;
        private int partitions;
        private short replicas;
    }
}
