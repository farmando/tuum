package com.tuum.interview.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuum.interview.model.RabbitMqEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import java.util.Properties;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RabbitMqEventProducerTest {
    @Mock
    private RabbitTemplate rabbitTemplateMock;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private RabbitAdmin rabbitAdmin;
    @InjectMocks
    private RabbitMqEventProducer producer;
    private static final String QUEUE_NAME = "Q.TUUM.INTERVIEW";

    @Test
    void sendMessage() throws JsonProcessingException {
        when(rabbitAdmin.getQueueProperties(any())).thenReturn(new Properties());
        RabbitMqEvent event = RabbitMqEvent.builder().idMessage(UUID.randomUUID()).typeEvent("ACC_CREATE").build();
        when(objectMapper.writeValueAsString(event)).thenReturn("something");

        assertThatCode(() -> producer.sendMessage(event)).doesNotThrowAnyException();

        Mockito.verify(rabbitTemplateMock).convertAndSend(QUEUE_NAME, "something");
    }
}