#!/usr/bin/env bash

echo
echo ">> parking service"
echo "> Create topic 'space-booking.inbox.events'"
echo "----------------------------------------"
docker exec -t zookeeper kafka-topics --create --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1 --topic space-booking.inbox.events

echo
echo "> Create topic 'space-booking.outbox.events'"
echo "-----------------------------------------"
docker exec -t zookeeper kafka-topics --create --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1 --topic space-booking.outbox.events

echo
echo ">> payment service"
echo "> Create topic 'payment.inbox.events'"
echo "----------------------------------------"
docker exec -t zookeeper kafka-topics --create --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1 --topic payment.inbox.events

echo
echo "> Create topic 'payment.outbox.events'"
echo "-----------------------------------------"
docker exec -t zookeeper kafka-topics --create --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1 --topic payment.outbox.events
