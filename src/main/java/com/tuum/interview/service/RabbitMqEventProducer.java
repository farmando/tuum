package com.tuum.interview.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuum.interview.model.RabbitMqEvent;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Properties;

@AllArgsConstructor
@Service
public class RabbitMqEventProducer {

    private RabbitTemplate rabbitTemplate;
    private ObjectMapper objectMapper;
    private RabbitAdmin rabbitAdmin;
    private static final Logger LOG = LoggerFactory.getLogger(RabbitMqEventProducer.class.getSimpleName());
    private static final String QUEUE_NAME = "Q.TUUM.INTERVIEW";

    public void sendMessage(RabbitMqEvent event) {

        Properties properties = rabbitAdmin.getQueueProperties(QUEUE_NAME);
        assert properties != null;

        try {
            rabbitTemplate.convertAndSend(QUEUE_NAME, objectMapper.writeValueAsString(event));
        } catch (IOException exception) {
            LOG.error("could not send event to rabbitmq. Event: {}", event);
        }

    }
}
