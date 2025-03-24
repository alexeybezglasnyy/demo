package com.example.demo.messages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;
    private final Queue queue;

    public MessageProducer(RabbitTemplate rabbitTemplate, Queue queue) {
        this.rabbitTemplate = rabbitTemplate;
        this.queue = queue;
    }

    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(queue.getName(), message);
        log.info("Sent message: {}", message);
    }
}
