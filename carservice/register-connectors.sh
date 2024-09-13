#!/bin/sh

http PUT http://localhost:8083/connectors/reservation-outbox-connector/config < reservation-service/reservation-outbox-connector.json
http PUT http://localhost:8083/connectors/parking-outbox-connector/config < parking-service/parking-outbox-connector.json
http PUT http://localhost:8083/connectors/payment-outbox-connector/config < payment-service/payment-outbox-connector.json
