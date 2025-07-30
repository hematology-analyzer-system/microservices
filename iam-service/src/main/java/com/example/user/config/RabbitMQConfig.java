package com.example.user.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public TopicExchange appExchange() {
        return new TopicExchange("appExchange");
    }
    // test the queue
    @Bean
    public Binding requestBinding(Queue requestQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(requestQueue).to(appExchange).with("request.key");
    }

    @Bean
    public Binding responseBinding(Queue responseQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(responseQueue).to(appExchange).with("response.key");
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
    public Queue queue() {
        return new Queue("queueName", true); // Create a durable queue named "queueName"
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

    // queue for iam-service

    @Bean
    public Queue registerQueue() {
        return new Queue("registerQueue", true); // Durable queue for registration events
    }

    @Bean
    public Binding registerBinding(Queue registerQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(registerQueue).to(appExchange).with("register.key");
    }
    
    @Bean
    public Queue loginQueue() {
        return new Queue("loginQueue", true); // Durable queue for login events
    }
    
    @Bean
    public Binding loginBinding(Queue loginQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(loginQueue).to(appExchange).with("login.key");
    }
    
    @Bean
    public Queue resendOtpQueue() {
        return new Queue("resendOtpQueue", true); // Durable queue for resend-otp events
    }
    
    @Bean
    public Binding resendOtpBinding(Queue resendOtpQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(resendOtpQueue).to(appExchange).with("resendotp.key");
    }
    
    @Bean
    public Queue roleCreateQueue() { return new Queue("roleCreateQueue", true); }
    @Bean
    public Queue roleListQueue() { return new Queue("roleListQueue", true); }
    @Bean
    public Queue roleGetQueue() { return new Queue("roleGetQueue", true); }
    @Bean
    public Queue roleDeleteQueue() { return new Queue("roleDeleteQueue", true); }
    @Bean
    public Queue roleAssignPrivilegeQueue() { return new Queue("roleAssignPrivilegeQueue", true); }
    @Bean
    public Queue roleRemovePrivilegeQueue() { return new Queue("roleRemovePrivilegeQueue", true); }

    @Bean
    public Binding roleCreateBinding(Queue roleCreateQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(roleCreateQueue).to(appExchange).with("role.create");
    }
    @Bean
    public Binding roleListBinding(Queue roleListQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(roleListQueue).to(appExchange).with("role.list");
    }
    @Bean
    public Binding roleGetBinding(Queue roleGetQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(roleGetQueue).to(appExchange).with("role.get");
    }
    @Bean
    public Binding roleDeleteBinding(Queue roleDeleteQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(roleDeleteQueue).to(appExchange).with("role.delete");
    }
    @Bean
    public Binding roleAssignPrivilegeBinding(Queue roleAssignPrivilegeQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(roleAssignPrivilegeQueue).to(appExchange).with("role.assignPrivilege");
    }
    @Bean
    public Binding roleRemovePrivilegeBinding(Queue roleRemovePrivilegeQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(roleRemovePrivilegeQueue).to(appExchange).with("role.removePrivilege");
    }
    @Bean
    public Queue userCreateQueue() { return new Queue("userCreateQueue", true); }
    @Bean
    public Queue userListQueue() { return new Queue("userListQueue", true); }
    @Bean
    public Queue userGetQueue() { return new Queue("userGetQueue", true); }
    @Bean
    public Queue userDeleteQueue() { return new Queue("userDeleteQueue", true); }

    @Bean
    public Binding userCreateBinding(Queue userCreateQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(userCreateQueue).to(appExchange).with("user.create");
    }
    @Bean
    public Binding userListBinding(Queue userListQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(userListQueue).to(appExchange).with("user.list");
    }
    @Bean
    public Binding userGetBinding(Queue userGetQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(userGetQueue).to(appExchange).with("user.get");
    }
    @Bean
    public Binding userDeleteBinding(Queue userDeleteQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(userDeleteQueue).to(appExchange).with("user.delete");
    }
}
