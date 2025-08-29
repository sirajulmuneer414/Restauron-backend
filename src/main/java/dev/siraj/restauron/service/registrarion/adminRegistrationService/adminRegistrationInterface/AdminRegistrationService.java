package dev.siraj.restauron.service.registrarion.adminRegistrationService.adminRegistrationInterface;

import dev.siraj.restauron.DTO.registration.AdminRegistrationDto;
import dev.siraj.restauron.entity.users.Admin;
import dev.siraj.restauron.entity.users.UserAll;

public interface AdminRegistrationService {
    boolean verifyAdminCode(String trim);

    boolean registerAdminAndSentOtpForVerification(AdminRegistrationDto adminRegistrationDto);

    Admin findAdminByUserAll(UserAll user);

    void otpVerificationSuccessfulEnumChange(Admin admin);

    void saveAdmin(Admin admin);
}
