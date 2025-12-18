package dev.siraj.restauron.service.registrarion.otpService;


import dev.siraj.restauron.DTO.registration.OtpDto;
import dev.siraj.restauron.entity.otpRegistration.OtpAndUser;
import dev.siraj.restauron.entity.restaurant.RestaurantRegistration;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.respository.otpRepository.OtpAndUserRepository;
import dev.siraj.restauron.service.registrarion.emailService.emailInterface.EmailService;
import dev.siraj.restauron.service.registrarion.otpService.otpInterface.OtpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

@Service
@Slf4j
public class OtpServiceImp implements OtpService {

    @Autowired
    private EmailService emailService;


    @Autowired
    private OtpAndUserRepository otpRepository;

    private static final int OTP_LENGTH = 6;
    private static final int EXPIRATION_MINUTES = 5;

    private String generateOtp(){
        Random random = new SecureRandom();
        StringBuilder otp = new StringBuilder();

        for(int i = 0; i < OTP_LENGTH; i++){
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }


    @Override
    public String sendOtpForRestaurantRegistration(String name, String email, String restaurant, Long registeredRestaurantId) {


            String otp = generateOtp();
        System.out.println(otp);
            emailService.sendOtpEmail(
                    email,
                    otp,
                    name,
                    restaurant,
                    "Restauron - Registration OTP"
            );

        OtpAndUser otpAndUser = new OtpAndUser(otp,email, LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES));

             otpRepository.save(otpAndUser);



        return otp;
    }




    @Override
    public boolean verifyOtpUsingEmail(OtpDto otpDto,String userEmail) {



        OtpAndUser otpAndUser = otpRepository.findByUserEmail(userEmail);

        System.out.println(otpAndUser.getOtp());
        if(otpDto.getReceivedTime().isBefore(otpAndUser.getOtpExpirationTime())){
            return Objects.equals(otpDto.getOtp(), otpAndUser.getOtp());

        }

        return false;
    }

    @Override
    public void resendOtpUsingEmail(String email) {



    }

    @Override
    public boolean resendOtpUsingRestaurantRegistrationId(RestaurantRegistration restaurantRegistration) {
       try {
           OtpAndUser otpAndUser = otpRepository.findByUserEmail(restaurantRegistration.getOwnerEmail());

           if(otpAndUser == null)return false;

           String otp = generateOtp();
           System.out.println(otp);

           otpAndUser.setOtp(otp);
           otpAndUser.setOtpCreatedTime(LocalDateTime.now());
           otpAndUser.setOtpExpirationTime(LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES));

           emailService.sendOtpEmail(
                   restaurantRegistration.getOwnerEmail(),
                   otp,
                   restaurantRegistration.getOwnerName(),
                   restaurantRegistration.getRestaurantName(),
                   "Restauron - Registration OTP (Resend)"
           );

           otpRepository.save(otpAndUser);
       } catch (Exception e) {
           return false;
       }
        return true;
    }

    @Override
    public void generateAndSendOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        log.info("Generated OTP {} for email {}", otp, email);

        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);
        OtpAndUser otpAndUser = otpRepository.findByUserEmail(email);

        if(otpAndUser == null)otpAndUser = new OtpAndUser();

        otpAndUser.setUserEmail(email);
        otpAndUser.setOtp(otp);
        otpAndUser.setOtpCreatedTime(LocalDateTime.now());
        otpAndUser.setOtpExpirationTime(expirationTime);
        otpRepository.save(otpAndUser);
        log.info("Saved OTP for email {} with expiration at {}", email, expirationTime);
        emailService.sendOtpEmail(email, otp, "User", null, "Your OTP Code");

    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        OtpAndUser otpAndUser = otpRepository.findByUserEmail(email);
                if(otpAndUser == null) throw new IllegalArgumentException("OTP not found. Please request a new one.");

        if (otpAndUser.getOtpExpirationTime().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otpAndUser);
            throw new IllegalArgumentException("OTP has expired. Please request a new one.");
        }

        if (!otpAndUser.getOtp().equals(otp)) {
            return false;
        }

        otpRepository.delete(otpAndUser); // OTP is valid, clean it up
        return true;
    }
}
