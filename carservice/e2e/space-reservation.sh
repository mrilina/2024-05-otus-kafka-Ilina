#!/bin/sh

echo
echo "Place a space reservation successfully"
http POST http://localhost:8080/api/v1/reservations < space-reservation-placement.json

echo "Place a space reservation with invalid payment, space should be released (compensation)"
http POST http://localhost:8080/api/v1/reservations < invalid-payment.json

echo "Place a space reservation with unavailable space"
http POST http://localhost:8080/api/v1/reservations < invalid-space-taken.json
