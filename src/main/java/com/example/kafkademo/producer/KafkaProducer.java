package com.example.kafkademo.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topicName;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate,
                         @Value("${app.kafka.topic}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void sendMessage(String message) {
        System.out.println("Producing message: " + message);
        kafkaTemplate.send(topicName, message);
    }
}
