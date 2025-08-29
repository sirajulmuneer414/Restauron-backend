package dev.siraj.restauron.DTO.registration;

import java.time.LocalDateTime;


public class OtpDto {

    private String email;

    private String otp;

    private LocalDateTime receivedTime;

    public OtpDto(){
        this.receivedTime = LocalDateTime.now();
    }

    public LocalDateTime getReceivedTime() {
        return receivedTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
