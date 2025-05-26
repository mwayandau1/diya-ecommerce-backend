

package com.diya.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Async
    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Password Reset Request");

            Context context = new Context();
            context.setVariable("token", token);
            context.setVariable("resetUrl", frontendUrl + "/reset-password?token=" + token);
            context.setVariable("userName", toEmail);

            String htmlContent = templateEngine.process("password-reset-email", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Password reset email sent to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Async
    public void sendOrderConfirmationEmail(String toEmail, String orderNumber, String orderDetails) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Order Confirmation #" + orderNumber);

            Context context = new Context();
            context.setVariable("orderNumber", orderNumber);
            context.setVariable("orderDetails", orderDetails);
            context.setVariable("orderUrl", frontendUrl + "/orders/" + orderNumber);
            context.setVariable("userName", toEmail);

            String htmlContent = templateEngine.process("order-confirmation-email", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Order confirmation email sent to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send order confirmation email to {}: {}", toEmail, e.getMessage());
        }
    }
}
