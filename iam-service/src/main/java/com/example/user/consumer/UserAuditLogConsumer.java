package com.example.user.consumer;

import com.example.user.model.UserAuditLog;
import com.example.user.repository.UserAuditLogRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class UserAuditLogConsumer {
    @Autowired
    private UserAuditLogRepository userAuditLogRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // @RabbitListener(queues = {
    //     "registerQueue", "loginQueue", "resendOtpQueue", "verifyOtpQueue", "forgotPasswordQueue", "verifyResetOtpQueue", "resetPasswordQueue", "verifyActivationOtpQueue", "activationQueue", "sendVerificationOtpQueue", "generateOtpQueue", "logoutQueue"
    // })
    // public void receiveMessage(UserAuditLog userAuditLog) {
    //     userAuditLogRepository.save(userAuditLog);
    // }
    @RabbitListener(queues = "registerQueue")
    public void receiveRegisterEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("REGISTER");
        userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        userAuditLogRepository.save(userAuditLog);
    }

    @RabbitListener(queues = "loginQueue")
    public void receiveLoginEvent(UserAuditLog userAuditLog) {
        // Action and message are already set by producer
        userAuditLog.setAction("LOGIN");
        userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        userAuditLogRepository.save(userAuditLog);
    }

    @RabbitListener(queues = "resendOtpQueue")
    public void receiveResendOtpEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("RESEND_OTP");
        userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        userAuditLogRepository.save(userAuditLog);
    }

    @RabbitListener(queues = "verifyOtpQueue")
    public void receiveVerifyOtpEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("VERIFY_OTP");
        userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        userAuditLogRepository.save(userAuditLog);
    }

    @RabbitListener(queues = "forgotPasswordQueue")
    public void receiveForgotPasswordEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("FORGOT_PASSWORD");
        userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        userAuditLogRepository.save(userAuditLog);
    }

    @RabbitListener(queues = "verifyResetOtpQueue")
    public void receiveVerifyResetOtpEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("VERIFY_RESET_OTP");
        userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        userAuditLogRepository.save(userAuditLog);
    }

    @RabbitListener(queues = "resetPasswordQueue")
    public void receiveResetPasswordEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("RESET_PASSWORD");
        userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        userAuditLogRepository.save(userAuditLog);
    }

    @RabbitListener(queues = "verifyActivationOtpQueue")
    public void receiveVerifyActivationOtpEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("VERIFY_ACTIVATION_OTP");
        userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        userAuditLogRepository.save(userAuditLog);
    }

    @RabbitListener(queues = "activationQueue")
    public void receiveActivationEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("ACTIVATION");
        userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        userAuditLogRepository.save(userAuditLog);
    }

    @RabbitListener(queues = "sendVerificationOtpQueue")
    public void receiveSendVerificationOtpEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("SEND_VERIFICATION_OTP");
        userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        userAuditLogRepository.save(userAuditLog);
    }

    @RabbitListener(queues = "generateOtpQueue")
    public void receiveGenerateOtpEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("GENERATE_OTP");
        userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        userAuditLogRepository.save(userAuditLog);
    }

    @RabbitListener(queues = "logoutQueue")
    public void receiveLogoutEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("LOGOUT");
        userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        userAuditLogRepository.save(userAuditLog);
    }

    // consumer for PrivilegeController
    // create privilege event
    // @RabbitListener(queues = "privilegeCreateQueue")
    // public void receivePrivilegeCreateEvent(UserAuditLog userAuditLog) {
    //     userAuditLog.setAction("PRIVILEGE_CREATE");
    //     userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
    //     userAuditLogRepository.save(userAuditLog);
    // }

    // list privileges event
    @RabbitListener(queues = "privilegeListQueue")
    public void receivePrivilegeListEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("PRIVILEGE_LIST");
        userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        userAuditLogRepository.save(userAuditLog);
    }

    // get privilege by id event
    @RabbitListener(queues = "privilegeGetQueue")
    public void receivePrivilegeGetEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("PRIVILEGE_GET");
        userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        userAuditLogRepository.save(userAuditLog);
    }

    // delete privilege event by id
    @RabbitListener(queues = "privilegeDeleteQueue")
    public void receivePrivilegeDeleteEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("PRIVILEGE_DELETE");
        userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        userAuditLogRepository.save(userAuditLog);
    }

    // consumer for RoleController
    // create role event
    @RabbitListener(queues = "roleQueue")
    public void receiveRoleCreateEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("ROLE_CREATE");
        userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        userAuditLogRepository.save(userAuditLog);
    }

    // update role event
    @RabbitListener(queues = "roleUpdateQueue")
    public void receiveRoleUpdateEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("ROLE_UPDATE");
        userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        userAuditLogRepository.save(userAuditLog);
    }

    // update role failed event
    @RabbitListener(queues = "roleUpdateFailedQueue")
    public void receiveRoleUpdateFailedEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("ROLE_UPDATE_FAILED");
        userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        userAuditLogRepository.save(userAuditLog);
    }

    // list roles event
    @RabbitListener(queues = "roleListQueue")
    public void receiveRoleListEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("ROLE_LIST");
        userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        userAuditLogRepository.save(userAuditLog);
    }

    // get role by id event
    @RabbitListener(queues = "roleGetQueue")
    public void receiveRoleGetEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("ROLE_GET");
        userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        userAuditLogRepository.save(userAuditLog);
    }

    // delete role event by id
    @RabbitListener(queues = "roleDeleteQueue")
    public void receiveRoleDeleteEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("ROLE_DELETE");
        userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        userAuditLogRepository.save(userAuditLog);
    }

    // consumer for UserController
    // create user event
    @RabbitListener(queues = "userQueue")
    public void receiveUserCreateEvent(UserAuditLog userAuditLog) {
        // Don't override action if already set, otherwise set default
        if (userAuditLog.getAction() == null || userAuditLog.getAction().isEmpty()) {
            userAuditLog.setAction("USER_CREATE");
        }
        userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        userAuditLogRepository.save(userAuditLog);
        messagingTemplate.convertAndSend("/topic/userCreated", userAuditLog);
    }

    // get user by id event
    @RabbitListener(queues = "userGetQueue")
    public void receiveUserGetEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("USER_GET");
        userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        userAuditLogRepository.save(userAuditLog);
    }

    // user lock
    @RabbitListener(queues = "userLockQueue")
    public void receiveUserLockEvent(UserAuditLog userAuditLog) {
        try {
            // Use the existing userAuditLog object and update it
            userAuditLog.setAction("LOCK_USER");
            userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
            
            userAuditLogRepository.save(userAuditLog);
            System.out.println("UserAuditLogConsumer - Saved lock event to MongoDB with action: LOCK_USER");
            
            // Send real-time WebSocket notification
            messagingTemplate.convertAndSend("/topic/userLocked", userAuditLog);
            System.out.println("UserAuditLogConsumer - Sent WebSocket notification for lock event");
        } catch (Exception e) {
            System.err.println("UserAuditLogConsumer - Error processing lock event: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // user unlock
    @RabbitListener(queues = "userUnlockQueue")
    public void receiveUserUnlockEvent(UserAuditLog userAuditLog) {
        try {
            // Use the existing userAuditLog object and update it
            userAuditLog.setAction("UNLOCK_USER");
            userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
            
            userAuditLogRepository.save(userAuditLog);
            System.out.println("UserAuditLogConsumer - Saved unlock event to MongoDB with action: UNLOCK_USER");
            
            // Send real-time WebSocket notification
            messagingTemplate.convertAndSend("/topic/userUnlocked", userAuditLog);
            System.out.println("UserAuditLogConsumer - Sent WebSocket notification for unlock event");
        } catch (Exception e) {
            System.err.println("UserAuditLogConsumer - Error processing unlock event: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // delete user event by id
    @RabbitListener(queues = "userDeleteQueue")
    public void receiveUserDeleteEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("DELETE_USER");
        userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        userAuditLogRepository.save(userAuditLog);
        messagingTemplate.convertAndSend("/topic/userDeleted", userAuditLog);
    }

    // upload user image event
    @RabbitListener(queues = "userUploadQueue")
    public void receiveUserUploadEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("USER_UPLOAD");
        userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        userAuditLogRepository.save(userAuditLog);
    }

    // update user by id event
    @RabbitListener(queues = "userUpdateQueue")
    public void receiveUserUpdateEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("USER_UPDATE");
        userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        userAuditLogRepository.save(userAuditLog);
    }

    // change user password event
    @RabbitListener(queues = "userChangePasswordQueue")
    public void receiveUserChangePasswordEvent(UserAuditLog userAuditLog) {
        userAuditLog.setAction("USER_CHANGE_PASSWORD");
        userAuditLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        userAuditLogRepository.save(userAuditLog);
    }


}
