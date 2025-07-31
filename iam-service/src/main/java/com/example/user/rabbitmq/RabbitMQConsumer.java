package com.example.user.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.amqp.core.Message;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;

@Service
public class RabbitMQConsumer {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMQConsumer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // Request queue listener: receives a message, processes, and sends a reply
    @RabbitListener(queues = "requestQueue", ackMode = "MANUAL")
    public void receiveRequest(Message message, Channel channel) throws IOException {
        String request = new String(message.getBody());
        System.out.println("Received request: " + request);
        String response = "Processed: " + request;
        rabbitTemplate.convertAndSend("responseQueue", response);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    // Response queue listener: receives a response message
    @RabbitListener(queues = "responseQueue", ackMode = "MANUAL")
    public void receiveResponse(Message message, Channel channel) throws IOException {
        String response = new String(message.getBody());
        System.out.println("Received response: " + response);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    // Consumer for registration events (routing key 'register.key')
    @RabbitListener(queues = "registerQueue", ackMode = "MANUAL")
    public void receiveRegisterEvent(Message message, Channel channel) throws IOException {
        String event = new String(message.getBody());
        System.out.println("[RabbitMQ] Received registration event: " + event);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    // Consumer for resend-otp events (routing key 'resendotp.key')
    @RabbitListener(queues = "resendOtpQueue", ackMode = "MANUAL")
    public void receiveResendOtpEvent(Message message, Channel channel) throws IOException {
        String event = new String(message.getBody());
        System.out.println("[RabbitMQ] Received resend-otp event: " + event);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    // Consumer for verify-otp events (routing key 'verifyotp.key')
    @RabbitListener(queues = "verifyOtpQueue", ackMode = "MANUAL")
    public void receiveVerifyOtpEvent(Message message, Channel channel) throws IOException {
        String event = new String(message.getBody());
        System.out.println("[RabbitMQ] Received verify-otp event: " + event);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = "loginQueue", ackMode = "MANUAL")
    public void receiveLoginEvent(Message message, Channel channel) throws IOException {
        String event = new String(message.getBody());
        System.out.println("[RabbitMQ] Received login event: " + event);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = "logoutQueue", ackMode = "MANUAL")
    public void receiveLogoutEvent(Message message, Channel channel) throws IOException {
        String event = new String(message.getBody());
        System.out.println("[RabbitMQ] Received logout event: " + event);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    // Consumer for verify-reset-otp events (routing key 'verifyresetotp.key')
    @RabbitListener(queues = "verifyResetOtpQueue", ackMode = "MANUAL")
    public void receiveVerifyResetOtpEvent(Message message, Channel channel) throws IOException {
        String event = new String(message.getBody());
        System.out.println("[RabbitMQ] Received verify-reset-otp event: " + event);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
    
    // Consumer for reset-password events (routing key 'resetpassword.key')
    @RabbitListener(queues = "resetPasswordQueue", ackMode = "MANUAL")
    public void receiveResetPasswordEvent(Message message, Channel channel) throws IOException {
        String event = new String(message.getBody());
        System.out.println("[RabbitMQ] Received reset-password event: " + event);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = "privilegeCreateQueue", ackMode = "MANUAL")
    public void receivePrivilegeCreateEvent(Message message, Channel channel) throws IOException {
        String event = new String(message.getBody());
        System.out.println("[RabbitMQ] Received privilege.create event: " + event);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = "privilegeListQueue", ackMode = "MANUAL")
    public void receivePrivilegeListEvent(Message message, Channel channel) throws IOException {
        String event = new String(message.getBody());
        System.out.println("[RabbitMQ] Received privilege.list event: " + event);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = "privilegeGetQueue", ackMode = "MANUAL")
    public void receivePrivilegeGetEvent(Message message, Channel channel) throws IOException {
        String event = new String(message.getBody());
        System.out.println("[RabbitMQ] Received privilege.get event: " + event);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = "privilegeDeleteQueue", ackMode = "MANUAL")
    public void receivePrivilegeDeleteEvent(Message message, Channel channel) throws IOException {
        String event = new String(message.getBody());
        System.out.println("[RabbitMQ] Received privilege.delete event: " + event);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = "roleAssignPrivilegeQueue", ackMode = "MANUAL")
    public void receiveRoleAssignPrivilegeEvent(Message message, Channel channel) throws IOException {
        String event = new String(message.getBody());
        System.out.println("[RabbitMQ] Received role.assignPrivilege event: " + event);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = "roleRemovePrivilegeQueue", ackMode = "MANUAL")
    public void receiveRoleRemovePrivilegeEvent(Message message, Channel channel) throws IOException {
        String event = new String(message.getBody());
        System.out.println("[RabbitMQ] Received role.removePrivilege event: " + event);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    // User cosumer

    @RabbitListener(queues = "userCreateQueue", ackMode = "MANUAL")
    public void receiveUserCreateEvent(Message message, Channel channel) throws IOException {
        String event = new String(message.getBody());
        System.out.println("[RabbitMQ] Received user.create event: " + event);
        // TODO: Add custom logic for user creation event
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = "userListQueue", ackMode = "MANUAL")
    public void receiveUserListEvent(Message message, Channel channel) throws IOException {
        String event = new String(message.getBody());
        System.out.println("[RabbitMQ] Received user.list event: " + event);
        // TODO: Add custom logic for user list event
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = "userGetQueue", ackMode = "MANUAL")
    public void receiveUserGetEvent(Message message, Channel channel) throws IOException {
        String event = new String(message.getBody());
        System.out.println("[RabbitMQ] Received user.get event: " + event);
        // TODO: Add custom logic for user get event
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = "userDeleteQueue", ackMode = "MANUAL")
    public void receiveUserDeleteEvent(Message message, Channel channel) throws IOException {
        String event = new String(message.getBody());
        System.out.println("[RabbitMQ] Received user.delete event: " + event);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
