package dev.siraj.restauron.service.registrarion.otpService.otpInterface;

import dev.siraj.restauron.DTO.registration.OtpDto;
import dev.siraj.restauron.entity.restaurant.RestaurantRegistration;
import dev.siraj.restauron.entity.users.UserAll;

public interface OtpService {

    String sendOtpForRestaurantRegistration(String name, String email, String restaurant, Long id);

    boolean verifyOtpUsingEmail(OtpDto otpDto, String userId);


    boolean resendOtpUsingRestaurantRegistrationId(RestaurantRegistration userId);

    void generateAndSendOtp(String email);

    boolean verifyOtp(String email, String otp);
}
