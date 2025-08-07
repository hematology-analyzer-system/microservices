package com.example.user.config;

// import lombok.Data;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public TopicExchange appExchange() {
        return new TopicExchange("appExchange");
    }
    // test the queue
    // @Bean
    // public Binding requestBinding(Queue requestQueue, TopicExchange appExchange) {
    //     return BindingBuilder.bind(requestQueue).to(appExchange).with("request.key");
    // }

    // @Bean
    // public Binding responseBinding(Queue responseQueue, TopicExchange appExchange) {
    //     return BindingBuilder.bind(responseQueue).to(appExchange).with("response.key");
    // }
    // @Bean
    // public Queue requestQueue() {
    //     return new Queue("requestQueue", true);
    // }

    // @Bean
    // public Queue responseQueue() {
    //     return new Queue("responseQueue", true);
    // }

    // @Bean
    // public Queue queue() {
    //     return new Queue("queueName", true); // Create a durable queue named "queueName"
    // }
    
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
    public Binding registerBinding(@Qualifier("registerQueue") Queue registerQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(registerQueue).to(appExchange).with("register.key");
    }

    // /login
    @Bean
    public Queue loginQueue() {
        return new Queue("loginQueue", true); // Durable queue for login events
    }

    @Bean
    public Binding loginBinding(@Qualifier("loginQueue") Queue loginQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(loginQueue).to(appExchange).with("login.key");
    }

    // /resend-otp
    @Bean
    public Queue resendOtpQueue() {
        return new Queue("resendOtpQueue", true); // Durable queue for resend OTP events
    }
    @Bean
    public Binding resendOtpBinding(@Qualifier("resendOtpQueue") Queue resendOtpQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(resendOtpQueue).to(appExchange).with("resendotp.key");
    }

    // /verify-otp
    @Bean
    public Queue verifyOtpQueue() {
        return new Queue("verifyOtpQueue", true); // Durable queue for verify OTP events
    }
    @Bean
    public Binding verifyOtpBinding(@Qualifier("verifyOtpQueue") Queue verifyOtpQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(verifyOtpQueue).to(appExchange).with("verifyotp.key");
    }

    // /forgot-password
    @Bean
    public Queue forgotPasswordQueue() {
        return new Queue("forgotPasswordQueue", true); // Durable queue for forgot password events
    }
    @Bean
    public Binding forgotPasswordBinding(@Qualifier("forgotPasswordQueue") Queue forgotPasswordQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(forgotPasswordQueue).to(appExchange).with("forgotpassword.key");
    }

    //  /verify-reset-otp
    @Bean
    public Queue verifyResetOtpQueue() {
        return new Queue("verifyResetOtpQueue", true); // Durable queue for verify reset OTP events
    }
    @Bean
    public Binding verifyResetOtpBinding(@Qualifier("verifyResetOtpQueue") Queue verifyResetOtpQueue, TopicExchange appExchange){
        return BindingBuilder.bind(verifyResetOtpQueue).to(appExchange).with("verifyresetotp.key");
    }

    // /reset-password
    @Bean
    public Queue resetPasswordQueue() {
        return new Queue("resetPasswordQueue", true); // Durable queue for reset password events
    }
    @Bean
    public Binding resetPasswordBinding(@Qualifier("resetPasswordQueue") Queue resetPasswordQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(resetPasswordQueue).to(appExchange).with("resetpassword.key");
    }

    // /verify-activation-otp
    @Bean
    public Queue verifyActivationOtpQueue() {
        return new Queue("verifyActivationOtpQueue", true); // Durable queue for verify activation OTP events
    }
    @Bean
    public Binding verifyActivationOtpBinding(@Qualifier("verifyActivationOtpQueue") Queue verifyActivationOtpQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(verifyActivationOtpQueue).to(appExchange).with("verifyactivationotp.key");
    }

    // /activation/{email}
    @Bean
    public Queue activationQueue() {
        return new Queue("activationQueue", true); // Durable queue for activation events
    }
    @Bean
    public Binding activationBinding(@Qualifier("activationQueue") Queue activationQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(activationQueue).to(appExchange).with("activation.key");
    }

    // sendVerificationOtp
    @Bean
    public Queue sendVerificationOtpQueue() {
        return new Queue("sendVerificationOtpQueue", true); // Durable queue for sending verification OTP
    }
    @Bean
    public Binding sendVerificationOtpBinding(@Qualifier("sendVerificationOtpQueue") Queue sendVerificationOtpQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(sendVerificationOtpQueue).to(appExchange).with("sendverificationotp.key");
    }

    // generateOtp
    @Bean
    public Queue generateOtpQueue() {
        return new Queue("generateOtpQueue", true); // Durable queue for generating OTP
    }
    @Bean
    public Binding generateOtpBinding(@Qualifier("generateOtpQueue") Queue generateOtpQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(generateOtpQueue).to(appExchange).with("generateotp.key");
    }

    // /logout
    @Bean
    public Queue logoutQueue() {
        return new Queue("logoutQueue", true); // Durable queue for logout events
    }
    @Bean
    public Binding logoutBinding(@Qualifier("logoutQueue") Queue logoutQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(logoutQueue).to(appExchange).with("logout.key");
    }

    // queue for PrivilegeController
    // createPrivilege
    // @Bean
    // public Queue privilegeCreateQueue() {
    //     return new Queue("privilegeCreateQueue", true); // Durable queue for privilege events
    // }
    // @Bean
    // public Binding createPrivilegeBinding(@Qualifier("privilegeCreateQueue") Queue privilegeCreateQueue, TopicExchange appExchange) {
    //     return BindingBuilder.bind(privilegeCreateQueue).to(appExchange).with("privilege.create");
    // }

    // listPrivileges
    @Bean
    public Queue privilegeListQueue() {
        return new Queue("privilegeListQueue", true); // Durable queue for listing privileges
    }
    @Bean
    public Binding listPrivilegesBinding(@Qualifier("privilegeListQueue") Queue privilegeListQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(privilegeListQueue).to(appExchange).with("privilege.list");
    }

    // get /{id}
    @Bean
    public Queue privilegeGetQueue() {
        return new Queue("privilegeGetQueue", true); // Durable queue for getting a privilege by ID
    }
    @Bean
    public Binding getPrivilegeBinding(@Qualifier("privilegeGetQueue") Queue privilegeGetQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(privilegeGetQueue).to(appExchange).with("privilege.get");
    }

    // delete /{id}
    @Bean
    public Queue privilegeDeleteQueue() {
        return new Queue("privilegeDeleteQueue", true); // Durable queue for deleting a privilege by ID
    }
    @Bean
    public Binding deletePrivilegeBinding(@Qualifier("privilegeDeleteQueue") Queue privilegeDeleteQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(privilegeDeleteQueue).to(appExchange).with("privilege.delete");
    }

    // queue for RoleController
    // createRole
    @Bean
    public Queue roleQueue() {
        return new Queue("roleQueue", true); // Durable queue for role events
    }
    @Bean
    public Binding createRoleBinding(@Qualifier("roleQueue") Queue roleQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(roleQueue).to(appExchange).with("role.create");
    }

    // updateRole
    @Bean
    public Queue roleUpdateQueue() {
        return new Queue("roleUpdateQueue", true); // Durable queue for updating roles
    }
    @Bean
    public Binding updateRoleBinding(@Qualifier("roleUpdateQueue") Queue roleUpdateQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(roleUpdateQueue).to(appExchange).with("role.update");
    }
    @Bean
    public Queue roleUpdateFailedQueue() {
        return new Queue("roleUpdateFailedQueue", true); // Durable queue for failed role updates
    }
    @Bean
    public Binding updateRoleFailedBinding(@Qualifier("roleUpdateFailedQueue") Queue roleUpdateFailedQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(roleUpdateFailedQueue).to(appExchange).with("role.update.failed");
    }

    // listRoles
    @Bean
    public Queue roleListQueue() {
        return new Queue("roleListQueue", true); // Durable queue for listing roles
    }
    @Bean
    public Binding listRolesBinding(@Qualifier("roleListQueue") Queue roleListQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(roleListQueue).to(appExchange).with("role.list");
    }

    // get /{id}
    @Bean
    public Queue roleGetQueue() {
        return new Queue("roleGetQueue", true); // Durable queue for getting a role by ID
    }
    @Bean
    public Binding getRoleBinding(@Qualifier("roleGetQueue") Queue roleGetQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(roleGetQueue).to(appExchange).with("role.get");
    }

    // delete /{id}
    @Bean
    public Queue roleDeleteQueue() {
        return new Queue("roleDeleteQueue", true); // Durable queue for deleting a role by ID
    }
    @Bean
    public Binding deleteRoleBinding(@Qualifier("roleDeleteQueue") Queue roleDeleteQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(roleDeleteQueue).to(appExchange).with("role.delete");
    }

    // queues for UserController
    // createUser
    @Bean
    public Queue userQueue() {
        return new Queue("userQueue", true); // Durable queue for user events
    }
    @Bean
    public Binding createUserBinding(@Qualifier("userQueue") Queue userQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(userQueue).to(appExchange).with("user.create");
    }

    // getUser /{id}
    @Bean
    public Queue userGetQueue() {
        return new Queue("userGetQueue", true); // Durable queue for getting a user by ID
    }
    @Bean
    public Binding getUserBinding(@Qualifier("userGetQueue") Queue userGetQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(userGetQueue).to(appExchange).with("user.get");
    }

    // deleteUser /{id}
    @Bean
    public Queue userDeleteQueue() {
        return new Queue("userDeleteQueue", true); // Durable queue for deleting a user by ID
    }
    @Bean
    public Binding deleteUserBinding(@Qualifier("userDeleteQueue") Queue userDeleteQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(userDeleteQueue).to(appExchange).with("user.delete");
    }

    // upload
    @Bean
    public Queue userUploadQueue() {
        return new Queue("userUploadQueue", true); // Durable queue for user upload events
    }
    @Bean
    public Binding userUploadBinding(@Qualifier("userUploadQueue") Queue userUploadQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(userUploadQueue).to(appExchange).with("user.uploadProfilePic");
    }

    // updateUser /{id}
    @Bean
    public Queue userUpdateQueue() {
        return new Queue("userUpdateQueue", true); // Durable queue for updating a user by ID
    }
    @Bean
    public Binding updateUserBinding(@Qualifier("userUpdateQueue") Queue userUpdateQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(userUpdateQueue).to(appExchange).with("user.update");
    }

    // /{userId}/change-password
    @Bean
    public Queue userChangePasswordQueue() {
        return new Queue("userChangePasswordQueue", true); // Durable queue for changing user password
    }
    @Bean
    public Binding userChangePasswordBinding(@Qualifier("userChangePasswordQueue") Queue userChangePasswordQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(userChangePasswordQueue).to(appExchange).with("user.changePassword");
    }
    @Bean
    public Queue userLockQueue() {
        return new Queue("userLockQueue", true); // Durable queue for locking a user
    }
    @Bean
    public Binding userLockBinding(@Qualifier("userLockQueue") Queue userLockQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(userLockQueue).to(appExchange).with("user.lock");
    }
    @Bean
    public Queue userUnlockQueue() {
        return new Queue("userUnlockQueue", true); // Durable queue for unlocking a user
    }
    @Bean
    public Binding userUnlockBinding(@Qualifier("userUnlockQueue") Queue userUnlockQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(userUnlockQueue).to(appExchange).with("user.unlock");
    }


}
