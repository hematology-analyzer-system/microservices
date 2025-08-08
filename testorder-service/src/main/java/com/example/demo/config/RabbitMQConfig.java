package com.example.demo.config;

import com.example.demo.messaging.util.JsonMessageConverterConfig;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * RabbitMQ configuration for testorder service
 * Following the pattern from RabbitMQ.md specification
 */
@Configuration
@Import(JsonMessageConverterConfig.class)
public class RabbitMQConfig {

    // Exchange names
    public static final String TESTORDER_EXCHANGE = "testorder.exchange";
    public static final String PATIENT_EXCHANGE = "patient.exchange";

    // Queue names for testorder events
    public static final String TESTORDER_CREATED_QUEUE = "testorder.created.q";
    public static final String TESTORDER_UPDATED_QUEUE = "testorder.updated.q";

    // Queue names for patient events consumption
    public static final String PATIENT_CREATED_QUEUE = "testorder.patient.created.q";
    public static final String PATIENT_UPDATED_QUEUE = "testorder.patient.updated.q";

    // Queue names for notification events
    public static final String NOTIFICATION_CREATED_QUEUE = "testorder.notification.created.q";
    public static final String NOTIFICATION_UPDATED_QUEUE = "testorder.notification.updated.q";
    public static final String NOTIFICATION_STATUS_QUEUE = "testorder.notification.status.q";
    public static final String NOTIFICATION_RESULT_QUEUE = "testorder.notification.result.q";

    // Routing keys
    public static final String TESTORDER_CREATED_ROUTING_KEY = "testorder.event.created.v1";
    public static final String TESTORDER_UPDATED_ROUTING_KEY = "testorder.event.updated.v1";
    public static final String PATIENT_CREATED_ROUTING_KEY = "patient.event.created.v1";
    public static final String PATIENT_UPDATED_ROUTING_KEY = "patient.event.updated.v1";

    // Notification routing keys
    public static final String NOTIFICATION_CREATED_ROUTING_KEY = "testorder.notification.created";
    public static final String NOTIFICATION_UPDATED_ROUTING_KEY = "testorder.notification.updated";
    public static final String NOTIFICATION_STATUS_ROUTING_KEY = "testorder.notification.status";
    public static final String NOTIFICATION_RESULT_ROUTING_KEY = "testorder.notification.result";

    @Autowired
    private MessageConverter messageConverter;

    // RabbitTemplate with JSON converter
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    // TestOrder Exchange
    @Bean
    public TopicExchange testorderExchange() {
        return new TopicExchange(TESTORDER_EXCHANGE);
    }

    // Patient Exchange (for consuming patient events)
    @Bean
    public TopicExchange patientExchange() {
        return new TopicExchange(PATIENT_EXCHANGE);
    }

    // Queues for TestOrder events (outgoing)
    @Bean
    public Queue testorderCreatedQueue() {
        return QueueBuilder.durable(TESTORDER_CREATED_QUEUE).build();
    }

    @Bean
    public Queue testorderUpdatedQueue() {
        return QueueBuilder.durable(TESTORDER_UPDATED_QUEUE).build();
    }

    // Queues for Patient events (incoming)
    @Bean
    public Queue patientCreatedQueue() {
        return QueueBuilder.durable(PATIENT_CREATED_QUEUE).build();
    }

    @Bean
    public Queue patientUpdatedQueue() {
        return QueueBuilder.durable(PATIENT_UPDATED_QUEUE).build();
    }

    // Bindings for TestOrder events
    @Bean
    public Binding testorderCreatedBinding() {
        return BindingBuilder
                .bind(testorderCreatedQueue())
                .to(testorderExchange())
                .with(TESTORDER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding testorderUpdatedBinding() {
        return BindingBuilder
                .bind(testorderUpdatedQueue())
                .to(testorderExchange())
                .with(TESTORDER_UPDATED_ROUTING_KEY);
    }

    // Bindings for Patient events
    @Bean
    public Binding patientCreatedBinding() {
        return BindingBuilder
                .bind(patientCreatedQueue())
                .to(patientExchange())
                .with(PATIENT_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding patientUpdatedBinding() {
        return BindingBuilder
                .bind(patientUpdatedQueue())
                .to(patientExchange())
                .with(PATIENT_UPDATED_ROUTING_KEY);
    }

    // ===================== NOTIFICATION QUEUES & BINDINGS =====================

    // Notification Queues
    @Bean
    public Queue notificationCreatedQueue() {
        return QueueBuilder.durable(NOTIFICATION_CREATED_QUEUE).build();
    }

    @Bean
    public Queue notificationUpdatedQueue() {
        return QueueBuilder.durable(NOTIFICATION_UPDATED_QUEUE).build();
    }

    @Bean
    public Queue notificationStatusQueue() {
        return QueueBuilder.durable(NOTIFICATION_STATUS_QUEUE).build();
    }

    @Bean
    public Queue notificationResultQueue() {
        return QueueBuilder.durable(NOTIFICATION_RESULT_QUEUE).build();
    }

    // Notification Bindings
    @Bean
    public Binding notificationCreatedBinding() {
        return BindingBuilder
                .bind(notificationCreatedQueue())
                .to(testorderExchange())
                .with(NOTIFICATION_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding notificationUpdatedBinding() {
        return BindingBuilder
                .bind(notificationUpdatedQueue())
                .to(testorderExchange())
                .with(NOTIFICATION_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding notificationStatusBinding() {
        return BindingBuilder
                .bind(notificationStatusQueue())
                .to(testorderExchange())
                .with(NOTIFICATION_STATUS_ROUTING_KEY);
    }

    @Bean
    public Binding notificationResultBinding() {
        return BindingBuilder
                .bind(notificationResultQueue())
                .to(testorderExchange())
                .with(NOTIFICATION_RESULT_ROUTING_KEY);
    }
}