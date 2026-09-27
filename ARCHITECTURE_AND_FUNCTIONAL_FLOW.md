# Kafka Demo: Architectural and Functional Flow

## 1. Overview

This project is a simple Spring Boot application demonstrating communication with Apache Kafka using String messages.

The application provides a REST endpoint that publishes a message to Kafka topic `demo-topic`. A Kafka consumer listens to the same topic and prints each received message to the console.

## 2. Technology Stack

- Java 21
- Spring Boot 3.5.6
- Maven
- Spring Web
- Spring for Apache Kafka
- Apache Kafka running on `localhost:9092`
- String-based Kafka messages

## 3. High-Level Architecture

```text
+------------------+
| Client / Postman  |
+---------+--------+
          |
          | HTTP POST
          v
+--------------------------+
| MessageController        |
| /api/v1/kafka/publish    |
+------------+-------------+
             |
             v
+--------------------------+
| KafkaProducer            |
| KafkaTemplate<String,String>
+------------+-------------+
             |
             | StringSerializer
             v
+--------------------------+
| Kafka Broker             |
| localhost:9092           |
| Topic: demo-topic        |
+------------+-------------+
             |
             | StringDeserializer
             v
+--------------------------+
| KafkaConsumer            |
| @KafkaListener           |
+------------+-------------+
             |
             v
       Console Output
```

## 4. Project Components

### 4.1 `KafkaDemoApplication`

The main Spring Boot application class. It starts the embedded web server and initializes all Spring-managed components.

### 4.2 `KafkaTopicConfig`

Creates the Kafka topic using Spring Kafka's `NewTopic` and `TopicBuilder` APIs.

Topic settings:

- Name: `${app.kafka.topic}`
- Partitions: `1`
- Replicas: `1`

The configured topic name is `demo-topic`.

### 4.3 `MessageController`

Exposes the REST endpoint:

```http
POST /api/v1/kafka/publish?message=hello%20world
```

It receives the `message` request parameter, delegates publishing to `KafkaProducer`, and returns a text response to the client.

### 4.4 `KafkaProducer`

Uses constructor injection to receive `KafkaTemplate<String, String>` and the configured topic name.

The producer:

1. Prints the outgoing message.
2. Sends the message to Kafka using `kafkaTemplate.send(topicName, message)`.

### 4.5 `KafkaConsumer`

Uses `@KafkaListener` to subscribe to the configured topic and consumer group.

When a message arrives, it prints the message to the console.

## 5. Kafka Configuration

Kafka is configured in `src/main/resources/application.properties`.

```properties
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=demo-group
spring.kafka.consumer.auto-offset-reset=earliest
app.kafka.topic=demo-topic
```

The producer uses:

```text
StringSerializer for keys
StringSerializer for values
```

The consumer uses:

```text
StringDeserializer for keys
StringDeserializer for values
```

## 6. Functional Flow

### Step 1: Application startup

1. `KafkaDemoApplication` starts Spring Boot.
2. Spring creates the controller, producer, consumer, and topic configuration beans.
3. Spring Kafka connects to `localhost:9092`.
4. The `demo-topic` topic is created or verified with one partition and one replica.
5. The consumer listener subscribes to `demo-topic` using group `demo-group`.

### Step 2: Client sends a request

The client sends:

```http
POST http://localhost:8080/api/v1/kafka/publish?message=hello%20world
```

### Step 3: Controller receives the request

`MessageController` reads the `message` request parameter:

```text
hello world
```

It then calls:

```java
kafkaProducer.sendMessage(message);
```

### Step 4: Producer publishes the message

`KafkaProducer` prints:

```text
Producing message: hello world
```

It then publishes the String message to `demo-topic` through `KafkaTemplate<String, String>`.

### Step 5: Kafka serializes and stores the message

The message value is converted to bytes by `StringSerializer` and stored in the Kafka topic.

Because the topic has one partition, the message is written to that single partition.

### Step 6: Consumer receives the message

The consumer group `demo-group` polls the topic. Kafka deserializes the message using `StringDeserializer` and passes the resulting String to:

```java
public void consume(String message)
```

### Step 7: Consumer prints the message

The application prints:

```text
Received message from Kafka: hello world
```

## 7. End-to-End Flow Summary

```text
HTTP client
    |
    | POST /api/v1/kafka/publish?message=hello world
    v
MessageController
    |
    | sendMessage(message)
    v
KafkaProducer
    |
    | KafkaTemplate.send("demo-topic", message)
    v
StringSerializer
    |
    v
Kafka Broker: localhost:9092
    |
    v
Topic: demo-topic, Partition: 0
    |
    v
StringDeserializer
    |
    v
KafkaConsumer
    |
    v
Console: Received message from Kafka: hello world
```

## 8. API Contract

### Publish message

**Endpoint**

```http
POST /api/v1/kafka/publish
```

**Request parameter**

| Parameter | Type | Required | Description |
|---|---|---:|---|
| `message` | String | Yes | Message to publish to Kafka |

**Example request**

```http
POST http://localhost:8080/api/v1/kafka/publish?message=hello%20world
```

**Successful response**

```text
Message sent to Kafka: hello world
```

## 9. Startup and Testing

### Prerequisites

1. Java 21 is installed.
2. Maven is installed.
3. Apache Kafka is running on `localhost:9092`.

### Start the application

```bash
mvn spring-boot:run
```

### Test with Postman

Create a `POST` request to:

```text
http://localhost:8080/api/v1/kafka/publish?message=hello%20world
```

Verify the following output:

```text
Message sent to Kafka: hello world
Producing message: hello world
Received message from Kafka: hello world
```

## 10. Error and Dependency Notes

- Kafka must be available before the Spring Boot application starts successfully.
- If Kafka is unavailable, the application may fail to create the topic or the consumer may repeatedly retry its connection.
- The topic configuration uses one partition and one replica for local development.
- The application sends plain String messages; it does not use JSON or custom message objects.
- The REST endpoint returns immediately after handing the message to KafkaTemplate. Kafka delivery is handled asynchronously by the Kafka producer client.

## 11. Design Principles Demonstrated

- Constructor-based dependency injection
- Externalized configuration using `application.properties`
- Separation of REST, producer, consumer, and configuration responsibilities
- Spring Kafka's `KafkaTemplate` for publishing
- Spring Kafka's `@KafkaListener` for consuming
- String serialization and deserialization
- Automatic topic declaration through a Spring configuration bean
