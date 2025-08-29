package dev.siraj.restauron.service.registrarion.emailService.emailInterface;

public interface EmailService {

    void sendEmail(String mailId, String body, String subject);

    void sendEmployeeCredentialsToEmail(String finalCompanyEmail, String generatedPassword, String personalEmail, String restaurantName);
}
