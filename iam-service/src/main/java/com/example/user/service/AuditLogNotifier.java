package com.example.user.service;

import com.example.user.model.UserAuditLog;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import jakarta.annotation.PostConstruct;

@Service
public class AuditLogNotifier {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostConstruct
    public void init() {
        Thread changeStreamThread = new Thread(() -> {
            var collection = mongoTemplate.getCollection("iamMessage");
            var cursor = collection.watch(List.of(Document.parse("{ 'operationType': 'insert' }"))).iterator();
            while (cursor.hasNext()) {
                var event = cursor.next();
                Document fullDoc = event.getFullDocument();
                if (fullDoc != null) {
                    UserAuditLog auditLog = mongoTemplate.getConverter().read(UserAuditLog.class, fullDoc);
                    messagingTemplate.convertAndSend("/topic/auditLog", auditLog);

                    // If this is a user creation event, also notify /topic/userCreated
                    if ("CREATE_USER".equals(auditLog.getAction())) {
                        // You can send the auditLog or a custom user object
                        messagingTemplate.convertAndSend("/topic/userCreated", auditLog);
                    }
                    // Disable lock/unlock notifications here to prevent duplicates
                    // These are handled by UserAuditLogConsumer
                    // if("LOCK_USER".equals(auditLog.getAction())) {
                    //     // Notify about user lock event
                    //     messagingTemplate.convertAndSend("/topic/userLocked", auditLog);
                    // }
                    // if("UNLOCK_USER".equals(auditLog.getAction())) {
                    //     // Notify about user unlock event
                    //     messagingTemplate.convertAndSend("/topic/userUnlocked", auditLog);
                    // }
                    if("DELETE_USER".equals(auditLog.getAction())) {
                        // Notify about user deletion event
                        messagingTemplate.convertAndSend("/topic/userDeleted", auditLog);
                    }

                }
            }
        });
        changeStreamThread.setDaemon(true);
        changeStreamThread.start();
    }
}
