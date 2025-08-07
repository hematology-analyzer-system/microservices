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


//package com.example.user.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import okhttp3.*;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.*;
//
//@Service
//public class EmailService {
//
//    @Value("${mailtrap.api.token}")
//    private String apiToken;
//
//    @Value("${mailtrap.sender.email}")
//    private String fromEmail;
//
//    @Value("${mailtrap.sender.name}")
//    private String fromName;
//
//    private static final String MAILTRAP_SEND_API = "https://send.api.mailtrap.io/api/send";
//
//    private final OkHttpClient httpClient = new OkHttpClient();
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Async
//    public void sendEmail(String to, String subject, String body) {
//        try {
//            // Build request body as a Map
//            Map<String, Object> emailData = new HashMap<>();
//            emailData.put("from", Map.of("email", fromEmail, "name", fromName));
//            emailData.put("to", List.of(Map.of("email", to)));
//            emailData.put("subject", subject);
//            emailData.put("text", body);
//            emailData.put("html", "<p>" + body + "</p>");
//
//            // Convert to JSON string
//            String json = objectMapper.writeValueAsString(emailData);
//
//            // Build OkHttp request
//            Request request = new Request.Builder()
//                    .url(MAILTRAP_SEND_API)
//                    .addHeader("Authorization", "Bearer " + apiToken)
//                    .addHeader("Content-Type", "application/json")
//                    .post(RequestBody.create(json, MediaType.parse("application/json")))
//                    .build();
//
//            // Execute request
//            Call call = httpClient.newCall(request);
//            Response response = call.execute();
//
//            if (!response.isSuccessful()) {
//                System.err.println("Failed to send email: " + response.code() + " " + response.body().string());
//            } else {
//                System.out.println("Email sent successfully to " + to);
//            }
//
//            response.close(); // Always close response
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}


//package com.example.user.service;
//import io.mailtrap.client.MailtrapClient;
//import io.mailtrap.config.MailtrapConfig;
//import io.mailtrap.factory.MailtrapClientFactory;
//import io.mailtrap.model.request.emails.Address;
//import io.mailtrap.model.request.emails.MailtrapMail;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class EmailService {
//
////    @Value("${mailtrap.api.token}")
//    private String apiToken;
//
//    @Value("${mailtrap.sender.email}")
//    private String fromEmail;
//
//    @Value("${mailtrap.sender.name}")
//    private String fromName;
//
//    @Async
//    public void sendEmail(String to, String subject, String body) {
//        try {
//            // Create Mailtrap config
//            apiToken ="7e63fdcef44191cbaa13eae947170f72";
//            MailtrapConfig config = new MailtrapConfig.Builder()
//                    .token(apiToken)
//                    .build();
//
//            // Create Mailtrap client
//            MailtrapClient client = MailtrapClientFactory.createMailtrapClient(config);
//
//            // Build the email
//            MailtrapMail mail = MailtrapMail.builder()
//                    .from(new Address(fromEmail, fromName))
//                    .to(List.of(new Address(to)))
//                    .subject(subject)
//                    .text(body)
//                    .html("<p>" + body + "</p>")
//                    .build();
//
//            // Send email
//            var response = client.send(mail);
//            System.out.println("Email sent successfully to " + to + " | Response: " + response);
//
//        } catch (Exception e) {
//            System.err.println("Failed to send email via Mailtrap: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}

package com.example.user.service;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${resend.api.key}")
    private String apiKey;

//    @Value("${resend.sender.email}")
    private String fromEmail; // e.g., "Acme <onboarding@resend.dev>"

    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            fromEmail = "Healthcare <no-reply@khoa.email>";
            Resend resend = new Resend(apiKey);

            CreateEmailOptions options = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(to)
                    .subject(subject)
                    .html("<p>" + body + "</p>")
                    .build();

            CreateEmailResponse response = resend.emails().send(options);
            System.out.println("Email sent successfully | ID: " + response.getId());
        } catch (Exception e) {
            System.err.println("Failed to send email via Resend: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
