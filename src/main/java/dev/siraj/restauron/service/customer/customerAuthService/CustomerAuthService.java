package dev.siraj.restauron.service.customer.customerAuthService;

import dev.siraj.restauron.DTO.customer.OTP.RegisterVerifyRequestDto;
import dev.siraj.restauron.DTO.customer.auth.AuthResponseDto;
import dev.siraj.restauron.DTO.customer.auth.GoogleLoginRequestDto;
import dev.siraj.restauron.DTO.customer.auth.LoginRequestDto;
import dev.siraj.restauron.DTO.customer.auth.RegisterRequestDto;

public interface CustomerAuthService {
    String register(RegisterRequestDto requestDto, String encryptedRestaurantId);

    String login(LoginRequestDto requestDto);

    AuthResponseDto processGoogleLogin(GoogleLoginRequestDto requestDto, String encryptedId);

    void sendRegistrationOtp(String email);

    String verifyAndRegister(RegisterVerifyRequestDto requestDto, String encryptedRestaurantId);
}
