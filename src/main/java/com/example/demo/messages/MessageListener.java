package com.example.demo.messages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static com.example.demo.utils.Constants.MY_QUEUE;

@Slf4j
@Service
public class MessageListener {

    @RabbitListener(queues = MY_QUEUE)
    public void receiveMessage(String message) {
        log.info("Received message: {}", message);
    }
}
