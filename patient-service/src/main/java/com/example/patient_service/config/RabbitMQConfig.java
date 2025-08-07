package com.example.patient_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    // Name of Exchange point for patient
    public static final String PATIENT_EXCHANGE = "patient.exchange";

    // Name of Queues for patient
    public static final String PATIENT_ADD_QUEUE = "patient.add.queue";
    public static final String PATIENT_UPDATE_QUEUE = "patient.update.queue";
    public static final String PATIENT_DELETE_QUEUE = "patient.delete.queue";

    // Name of Routing keys for patient
    public static final String PATIENT_ADD_ROUTING_KEY = "patient.add.routing.key";
    public static final String PATIENT_UPDATE_ROUTING_KEY = "patient.update.routing.key";
    public static final String PATIENT_DELETE_ROUTING_KEY = "patient.delete.routing.key";


    /* ************************************* */
    // 1. Create exchange point for RabbitMQ //
    /* ************************************* */
    @Bean
    public TopicExchange patientTopicExchange() {
        return new TopicExchange(PATIENT_EXCHANGE);
    }


    /* ************************************* */
    // 2. Create queues for story data       //
    /* ************************************* */
    @Bean
    public Queue patientAddQueue() {
        return new Queue(PATIENT_ADD_QUEUE);
    }

    @Bean
    public Queue patientUpdateQueue() {
        return new Queue(PATIENT_UPDATE_QUEUE);
    }

    @Bean
    public Queue patientDeleteQueue() {
        return new Queue(PATIENT_DELETE_QUEUE);
    }


    /* ********************************************************* */
    // 3. Binding EXCHANGE to QUEUE through exact ROUTING_KEY    //
    /* ********************************************************* */
    @Bean
    public Binding patientAddBinding(Queue patientAddQueue, TopicExchange patientTopicExchange) {
        return BindingBuilder.bind(patientAddQueue).to(patientTopicExchange).with(PATIENT_ADD_ROUTING_KEY);
    }

    @Bean
    public Binding patientUpdateBinding(Queue patientUpdateQueue, TopicExchange patientTopicExchange) {
        return BindingBuilder.bind(patientUpdateQueue).to(patientTopicExchange).with(PATIENT_UPDATE_ROUTING_KEY);
    }

    @Bean
    public Binding patientDeleteBinding(Queue patientDeleteQueue, TopicExchange patientTopicExchange) {
        return BindingBuilder.bind(patientDeleteQueue).to(patientTopicExchange).with(PATIENT_DELETE_ROUTING_KEY);
    }


    /* ******************************************************************** */
    // Create configuration used for RabbitMQ serialize/deserialize JSON    //
    /* ******************************************************************** */
    @Bean
    public MessageConverter JSONMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
