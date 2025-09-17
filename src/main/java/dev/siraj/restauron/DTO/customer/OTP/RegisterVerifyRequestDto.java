package dev.siraj.restauron.DTO.customer.OTP;

import lombok.Data;

@Data
public class RegisterVerifyRequestDto {
    private String name;
    private String email;
    private String password;
    private String phone;
    private String otp;
}
