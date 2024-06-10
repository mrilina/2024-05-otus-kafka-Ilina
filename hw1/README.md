Запуск контейнеров
```shell
docker compose up -d
```

Список контейнеров
```shell
docker ps -a
```
![docker_compose_up.png](docker_compose_up.png)

Создать топик
```shell
docker exec -ti kafka1 /usr/bin/kafka-topics --create --topic testtopic --partitions 1 --replication-factor 1 --bootstrap-server localhost:9092
```
![create_topic.png](create_topic.png)

Получить список топиков
```shell
docker exec -ti kafka1 /usr/bin/kafka-topics --list --bootstrap-server localhost:9092
```
![get_topic.png](get_topic.png)

Отправить сообщение
Каждая строка - одно сообщение. Прервать - Ctrl+Z
```shell
docker exec -ti kafka1 /usr/bin/kafka-console-producer --topic testtopic --bootstrap-server localhost:9092
```
![send_topic.png](send_topic.png)

Получить сообщения по consumer-group consumer1
```shell
docker exec -ti kafka1 /usr/bin/kafka-console-consumer --group consumer1 --from-beginning --topic testtopic --bootstrap-server localhost:9092  
```
![read_topic.png](read_topic.png)

Остановить контейнеры
```shell
docker compose down
```
![docker_compose_down.png](docker_compose_down.png)