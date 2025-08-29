package dev.siraj.restauron.entity.otpRegistration;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class OtpAndUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;

    private String otp;

    private LocalDateTime otpCreatedTime;

    private LocalDateTime otpExpirationTime;

    public OtpAndUser(){

    }


    public OtpAndUser(String otp, String userEmail, LocalDateTime expirationTime) {
        this.otp = otp;
        this.userEmail = userEmail;
        this.otpCreatedTime = LocalDateTime.now();
        this.otpExpirationTime = expirationTime;
    }

    public OtpAndUser(String otp, String userEmail, LocalDateTime creationTime , LocalDateTime expirationTime) {
        this.otp = otp;
        this.userEmail = userEmail;
        this.otpCreatedTime = creationTime;
        this.otpExpirationTime = expirationTime;
    }

    public Long getId() {
        return id;
    }


    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public LocalDateTime getOtpCreatedTime() {
        return otpCreatedTime;
    }

    public void setOtpCreatedTime(LocalDateTime otpCreatedTime) {
        this.otpCreatedTime = otpCreatedTime;
    }

    public LocalDateTime getOtpExpirationTime() {
        return otpExpirationTime;
    }

    public void setOtpExpirationTime(LocalDateTime otpExpirationTime) {
        this.otpExpirationTime = otpExpirationTime;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
