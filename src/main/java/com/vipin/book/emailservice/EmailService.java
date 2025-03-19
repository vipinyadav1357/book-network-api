package com.vipin.book.emailservice;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Async // we don't want to wait user to recieve email so asynchronous
    public void sendEmail(String to, String username, EMailTemplateName emailTemplateName, String confirmationUrl,
                          String activationCode, String subject) throws MessagingException {
        String templateName;
        if (emailTemplateName == null) {
            templateName = "confirm-email";
        } else {
            templateName = emailTemplateName.getName();
        }
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name());
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("confirmationUrl", confirmationUrl);
        properties.put("activationCode", activationCode);

        Context context = new Context();
        context.setVariables(properties);

        helper.setFrom("yadavvipinysy063@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        String template = templateEngine.process(templateName, context);
        helper.setText(template, true);
        javaMailSender.send(message);
    }
}
