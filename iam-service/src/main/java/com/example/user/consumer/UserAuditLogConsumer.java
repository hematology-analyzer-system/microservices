package com.example.user.consumer;

import com.example.user.model.UserAuditLog;
import com.example.user.repository.UserAuditLogRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserAuditLogConsumer {
    @Autowired
    private UserAuditLogRepository userAuditLogRepository;

    // @RabbitListener(queues = {
    //     "registerQueue", "loginQueue", "resendOtpQueue", "verifyOtpQueue", "forgotPasswordQueue", "verifyResetOtpQueue", "resetPasswordQueue", "verifyActivationOtpQueue", "activationQueue", "sendVerificationOtpQueue", "generateOtpQueue", "logoutQueue"
    // })
    // public void receiveMessage(UserAuditLog userAuditLog) {
    //     userAuditLogRepository.save(userAuditLog);
    // }
    @RabbitListener(queues = "registerQueue")
    public void receiveRegisterEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("REGISTER");
        
        userAuditLogRepository.save(userAuditLog);
    }

    @RabbitListener(queues = "loginQueue")
    public void receiveLoginEvent(UserAuditLog userAuditLog) {
        // Action and message are already set by producer
        userAuditLogRepository.save(userAuditLog);
    }

    @RabbitListener(queues = "resendOtpQueue")
    public void receiveResendOtpEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("RESEND_OTP");
        userAuditLogRepository.save(userAuditLog);
    }

    @RabbitListener(queues = "verifyOtpQueue")
    public void receiveVerifyOtpEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("VERIFY_OTP");
        userAuditLogRepository.save(userAuditLog);
    }

    @RabbitListener(queues = "forgotPasswordQueue")
    public void receiveForgotPasswordEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("FORGOT_PASSWORD");
        userAuditLogRepository.save(userAuditLog);
    }

    @RabbitListener(queues = "verifyResetOtpQueue")
    public void receiveVerifyResetOtpEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("VERIFY_RESET_OTP");
        userAuditLogRepository.save(userAuditLog);
    }

    @RabbitListener(queues = "resetPasswordQueue")
    public void receiveResetPasswordEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("RESET_PASSWORD");
        userAuditLogRepository.save(userAuditLog);
    }

    @RabbitListener(queues = "verifyActivationOtpQueue")
    public void receiveVerifyActivationOtpEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("VERIFY_ACTIVATION_OTP");
        userAuditLogRepository.save(userAuditLog);
    }

    @RabbitListener(queues = "activationQueue")
    public void receiveActivationEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("ACTIVATION");
        userAuditLogRepository.save(userAuditLog);
    }

    @RabbitListener(queues = "sendVerificationOtpQueue")
    public void receiveSendVerificationOtpEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("SEND_VERIFICATION_OTP");
        userAuditLogRepository.save(userAuditLog);
    }

    @RabbitListener(queues = "generateOtpQueue")
    public void receiveGenerateOtpEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("GENERATE_OTP");
        userAuditLogRepository.save(userAuditLog);
    }

    @RabbitListener(queues = "logoutQueue")
    public void receiveLogoutEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("LOGOUT");
        userAuditLogRepository.save(userAuditLog);
    }
}
