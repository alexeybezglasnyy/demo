package com.test.example.demo.messages;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.test.example.demo.utils.Constants.MY_QUEUE;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue myQueue() {
        return new Queue(MY_QUEUE, true);
    }
}
