package dev.siraj.restauron.service.registrarion.otpService.otpInterface;

import dev.siraj.restauron.DTO.registration.OtpDto;
import dev.siraj.restauron.entity.restaurant.RestaurantRegistration;
import dev.siraj.restauron.entity.users.UserAll;

public interface OtpService {

    String sendOtpForRestaurantRegistration(String name, String email, String restaurant, Long id);

    String sendOtpForAdminRegistration(String name, String email,Long id);

    boolean verifyOtpUsingEmail(OtpDto otpDto, String userId);

    void resendOtpUsingEmail(String email);

    boolean resendOtpUsingRestaurantRegistrationId(RestaurantRegistration userId);

    boolean resendOtpUsingAdminId(UserAll user, String email);

    void generateAndSendOtp(String email);

    boolean verifyOtp(String email, String otp);
}
