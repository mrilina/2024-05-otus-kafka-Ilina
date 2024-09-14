package org.carservice.reservation;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MessagingCDCConfig {


    @Bean
    NewTopic spaceBookingInboxTopic() {
        return new NewTopic("space-booking.inbox.events", 1, (short) 1);
    }

    @Bean
    NewTopic paymentInboxTopic() {
        return new NewTopic("payment.inbox.events", 1, (short) 1);
    }

    @Bean
    NewTopic spaceBookingOutboxTopic() {
        return new NewTopic("space-booking.outbox.events", 1, (short) 1);
    }

    @Bean
    NewTopic paymentOutboxTopic() {
        return new NewTopic("payment.outbox.events", 1, (short) 1);
    }

}
