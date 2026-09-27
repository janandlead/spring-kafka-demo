# Kafka Demo

A beginner-friendly Spring Boot application demonstrating a simple Apache Kafka producer and consumer using String messages.

## Technology stack

- Java 21
- Spring Boot 3.5.6
- Maven
- Spring Web
- Spring for Apache Kafka
- Apache Kafka on `localhost:9092`

## Application flow

```text
Client / Postman
      |
      | POST /api/v1/kafka/publish?message=hello%20world
      v
MessageController
      |
      v
KafkaProducer
      |
      | KafkaTemplate<String, String>
      v
Kafka Broker: localhost:9092
      |
      | Topic: demo-topic
      v
KafkaConsumer
      |
      v
Console output
```

The topic `demo-topic` is declared with one partition and one replica by `KafkaTopicConfig`.

For the complete architectural and functional flow, see [ARCHITECTURE_AND_FUNCTIONAL_FLOW.md](ARCHITECTURE_AND_FUNCTIONAL_FLOW.md).

## Project structure

```text
kafka-demo
├── pom.xml
├── ARCHITECTURE_AND_FUNCTIONAL_FLOW.md
├── src/main/java/com/example/kafkademo
│   ├── KafkaDemoApplication.java
│   ├── config/KafkaTopicConfig.java
│   ├── controller/MessageController.java
│   ├── producer/KafkaProducer.java
│   └── consumer/KafkaConsumer.java
└── src/main/resources/application.properties
```

## Configuration

Kafka and application settings are stored in `src/main/resources/application.properties`:

```properties
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=demo-group
app.kafka.topic=demo-topic
server.port=8080
```

The producer and consumer use `StringSerializer` and `StringDeserializer` for String messages.

## Prerequisites

Make sure the following are installed and available:

1. Java 21
2. Maven
3. Apache Kafka running on `localhost:9092`

## Start the application

From the project directory, run:

```bash
mvn spring-boot:run
```

Alternatively, build and run the packaged application:

```bash
mvn clean package
java -jar target/kafka-demo-0.0.1-SNAPSHOT.jar
```

## Test the producer endpoint

Using Postman or another HTTP client, send a `POST` request:

```http
POST http://localhost:8080/api/v1/kafka/publish?message=hello%20world
```

Expected API response:

```text
Message sent to Kafka: hello world
```

Expected producer log:

```text
Producing message: hello world
```

## Consumer output

The `KafkaConsumer` listens to `demo-topic` using consumer group `demo-group`. When Kafka delivers the message, the consumer deserializes it as a String and prints:

```text
Received message from Kafka: hello world
```

## Notes

- This example uses plain String messages, not JSON.
- Constructor injection is used throughout the application.
- No Lombok or Docker is required.
- Kafka must be running before starting the Spring Boot application.
