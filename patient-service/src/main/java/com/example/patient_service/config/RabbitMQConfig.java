package com.example.patient_service.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public org.springframework.amqp.core.TopicExchange appExchange() {
        return new org.springframework.amqp.core.TopicExchange("appExchange");
    }

    @Bean
    public org.springframework.amqp.core.Binding requestBinding(Queue requestQueue, org.springframework.amqp.core.TopicExchange appExchange) {
        return org.springframework.amqp.core.BindingBuilder.bind(requestQueue).to(appExchange).with("request.key");
    }

    @Bean
    public org.springframework.amqp.core.Binding responseBinding(Queue responseQueue, org.springframework.amqp.core.TopicExchange appExchange) {
        return org.springframework.amqp.core.BindingBuilder.bind(responseQueue).to(appExchange).with("response.key");
    }
    @Bean
    public Queue requestQueue() {
        return new Queue("requestQueue", true);
    }

    @Bean
    public Queue responseQueue() {
        return new Queue("responseQueue", true);
    }

    @Bean
    public TopicExchange patientExchange() {
        return new TopicExchange("patientExchange");
    }

    @Bean
    public Queue patientQueue() {
        return new Queue("patientQueue", true);
    }

    @Bean
    public Binding binding(Queue patientQueue, TopicExchange patientExchange) {
        return BindingBuilder.bind(patientQueue).to(patientExchange).with("patient.created");
    }
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter(); // Use JSON message converter
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter()); // Set the message converter
        return rabbitTemplate; // Return the configured RabbitTemplate
    }
}
