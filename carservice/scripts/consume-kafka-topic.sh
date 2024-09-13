#!/bin/sh

echo
echo "Topic 'space-booking.inbox.events' messages:"
docker exec -t zookeeper  kafka-console-consumer --topic space-booking.inbox.events --from-beginning --bootstrap-server kafka:9092

echo
echo "Topic 'space-book.outbox.events' messages:"
docker exec -t zookeeper  kafka-console-consumer --topic space-booking.outbox.events --from-beginning --bootstrap-server kafka:9092

echo
echo "Topic 'payment.inbox.events' messages:"
docker exec -t zookeeper  kafka-console-consumer --topic payment.outbox.events --from-beginning --bootstrap-server kafka:9092

echo
echo "Topic 'payment.outbox.events' messages:"
docker exec -t zookeeper  kafka-console-consumer --topic payment.outbox.events --from-beginning --bootstrap-server kafka:9092
