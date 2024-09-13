#!/bin/sh

# httpie
echo
echo "Place a space reservation successfully"
http POST http://localhost:8080/api/v1/reservations < space-reservation-placement.json

echo "Place a space reservation with invalid payment, space should be released (compensation)"
http POST http://localhost:8080/api/v1/reservations < invalid-payment.json

echo "Place a space reservation with unavailable space"
http POST http://localhost:8080/api/v1/reservations < invalid-space-taken.json

# curl
#echo
#echo "Place a space reservation successfully"
#curl -i -X POST -H Accept:application/json -H Content-Type:application/json http://localhost:8080/api/v1/reservations -d @space-reservation-placement.json

#echo
#echo "Place a space reservation with invalid payment, space should be released (compensation)"
#curl -i -X POST -H Accept:application/json -H Content-Type:application/json http://localhost:8080/api/v1/reservations -d @invalid-payment.json

#echo
#echo "Place a space reservation with unavailable space"
#curl -i -X POST -H Accept:application/json -H Content-Type:application/json http://localhost:8080/api/v1/reservations -d @invalid-space-taken.json
