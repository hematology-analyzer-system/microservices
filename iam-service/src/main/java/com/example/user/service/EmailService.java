//package com.example.user.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//@Service
//public class EmailService {
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    @Value("${spring.mail.username}")
//    private String from;
//
//    @Async
//    public void sendEmail(String to, String subject, String body) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom("no-reply@khoa.email");
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(body);
//        mailSender.send(message);
//    }
//}


package com.example.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class EmailService {

    @Value("${mailtrap.api.token}")
    private String apiToken;

    @Value("${mailtrap.sender.email}")
    private String fromEmail;

    @Value("${mailtrap.sender.name}")
    private String fromName;

    private static final String MAILTRAP_SEND_API = "https://send.api.mailtrap.io/api/send";

    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            // Build request body as a Map
            Map<String, Object> emailData = new HashMap<>();
            emailData.put("from", Map.of("email", fromEmail, "name", fromName));
            emailData.put("to", List.of(Map.of("email", to)));
            emailData.put("subject", subject);
            emailData.put("text", body);
            emailData.put("html", "<p>" + body + "</p>");

            // Convert to JSON string
            String json = objectMapper.writeValueAsString(emailData);

            // Build OkHttp request
            Request request = new Request.Builder()
                    .url(MAILTRAP_SEND_API)
                    .addHeader("Authorization", "Bearer " + apiToken)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(json, MediaType.parse("application/json")))
                    .build();

            // Execute request
            Call call = httpClient.newCall(request);
            Response response = call.execute();

            if (!response.isSuccessful()) {
                System.err.println("Failed to send email: " + response.code() + " " + response.body().string());
            } else {
                System.out.println("Email sent successfully to " + to);
            }

            response.close(); // Always close response
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
