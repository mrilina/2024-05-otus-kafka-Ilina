## Запуск брокера kafka

```shell
  docker compose up -d
```

## Создание топика mytopic

```shell
 docker exec -ti 774 bash
```

```shell
kafka-topics --create --topic mytopic2 --bootstrap-server localhost:9092
```

## Запуск Application

## Внутри контейнера kafka отправить сообщения key:value

```shell
kafka-console-producer --broker-list localhost:9092 --topic mytopic2 --property "parse.key=true" --property "key.separator=:"
```

```shell
>test1:msg1
>test2:mm1
>test1:msg2
```
