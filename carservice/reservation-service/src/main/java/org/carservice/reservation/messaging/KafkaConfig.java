package org.carservice.reservation.messaging;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
class KafkaConfig {

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, SpaceBookingEvent> spaceBookingKLCFactory(KafkaProperties props) {
        ConcurrentKafkaListenerContainerFactory<String, SpaceBookingEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(spaceBookingConsumerFactory(props));
        factory.setConcurrency(props.getListener().getConcurrency());
        return factory;
    }

    @Bean
    ConsumerFactory<String, SpaceBookingEvent> spaceBookingConsumerFactory(KafkaProperties props) {
        return new DefaultKafkaConsumerFactory<>(
                props.buildConsumerProperties(null),
                new StringDeserializer(),
                new JsonDeserializer<>(SpaceBookingEvent.class)
        );
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, PaymentEvent> paymentKLCFactory(KafkaProperties props) {
        ConcurrentKafkaListenerContainerFactory<String, PaymentEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(paymentConsumerFactory(props));
        factory.setConcurrency(props.getListener().getConcurrency());
        return factory;
    }

    @Bean
    ConsumerFactory<String, PaymentEvent> paymentConsumerFactory(KafkaProperties props) {
        return new DefaultKafkaConsumerFactory<>(
                props.buildConsumerProperties(null),
                new StringDeserializer(),
                new JsonDeserializer<>(PaymentEvent.class)
        );
    }
}
