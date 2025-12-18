package dev.siraj.restauron.service.registrarion.emailService.emailInterface;

import org.springframework.scheduling.annotation.Async;

public interface EmailService {

    void sendEmail(String mailId, String body, String subject);

    @Async
    void sendOtpEmail(String toEmail, String otp, String name, String restaurantName, String subject);

    void sendEmployeeCredentialsToEmail(String finalCompanyEmail, String generatedPassword, String personalEmail, String restaurantName);

    void sendSubscriptionExpiredEmail(String email, String restaurantName);

    void sendWarningEmail(String email,String restaurantName, int daysLeft);

    void sendPasswordResetEmail(String toEmail, String resetLink, String userName);
}
