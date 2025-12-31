package dev.siraj.restauron.restController.registration;

import dev.siraj.restauron.DTO.registration.*;
import dev.siraj.restauron.entity.enums.VerificationStatus;
import dev.siraj.restauron.entity.restaurant.RestaurantRegistration;
import dev.siraj.restauron.entity.users.Admin;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.service.registrarion.adminRegistrationService.adminRegistrationInterface.AdminRegistrationService;
import dev.siraj.restauron.service.registrarion.otpService.otpInterface.OtpService;
import dev.siraj.restauron.service.registrarion.registrationInitialService.registrationInitialInterface.RestaurantInitialService;
import dev.siraj.restauron.service.userService.UserServiceInterface.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
//@CrossOrigin(origins = "http://localhost:5173/")
@RequestMapping("/api/registration")
public class RegistrationController {

    private final RestaurantInitialService restaurantInitialService;

    private final UserService userService;

    private final OtpService otpService;

    @Autowired
    public RegistrationController(RestaurantInitialService restaurantInitialService, UserService userService, OtpService otpService) {
        this.restaurantInitialService = restaurantInitialService;
        this.userService = userService;
        this.otpService = otpService;
    }


    @PostMapping("/check-email")
    public ResponseEntity<Boolean> checkRegistrationEmail(@RequestBody EmailCheckingDto emailCheckingDto){
        log.info("In the controller method to check whether email exists or not");
        boolean exists = false;
        String email = emailCheckingDto.getEmail();
        String option = emailCheckingDto.getOption();
        log.info(email);
        log.info(option);
        if(option.equals("restaurant")){
            log.info("inside restaurant option method");
            exists = restaurantInitialService.registrationEmailExists(email);


            if(exists){
                RestaurantRegistration restaurantRegistration = restaurantInitialService.findByUserEmail(email);
                if(restaurantRegistration.getOtpStatus() == VerificationStatus.PENDING){
                    return new ResponseEntity<>(false, HttpStatus.OK);
                }
            }

            else{

                exists = userService.userExistsByEmailId(email);

            }




        }
        else{
            exists = userService.userExistsByEmailId(email);
        }

        return new ResponseEntity<Boolean>(exists, HttpStatus.OK);
    }


    @PostMapping("/restaurant")
    @Transactional
    public ResponseEntity<Boolean> registerRestaurant(@ModelAttribute RestaurantRegistrationDto restaurantRegistrationDto){

        log.info(restaurantRegistrationDto.getRestaurantEmail());
        log.info(restaurantRegistrationDto.getPassword());


        RestaurantRegistration registeredRestaurant = restaurantInitialService.registerRestaurantForApproval(restaurantRegistrationDto);

        otpService.sendOtpForRestaurantRegistration(restaurantRegistrationDto.getName(),restaurantRegistrationDto.getEmail(), restaurantRegistrationDto.getRestaurantName(), registeredRestaurant.getId());


        return new ResponseEntity<>(true,HttpStatus.CREATED);
       
    }


    @PostMapping("/restaurant/verify-otp")
    public ResponseEntity<Boolean> verifyOtpRestaurant(@RequestBody OtpDto otpDto){

         Long restaurantRegistrationId = restaurantInitialService.findRestaurantRegisterationOwnerIdByEmail(otpDto.getEmail());

        log.info("In restaurant otp verification method");



        if(!otpService.verifyOtpUsingEmail(otpDto, otpDto.getEmail())){

            return new ResponseEntity<>(false,HttpStatus.NOT_FOUND);

        }

        restaurantInitialService.otpVerificationSuccessfulEnumChange(restaurantRegistrationId);


        return new ResponseEntity<>(true,HttpStatus.ACCEPTED);


    }

    @PostMapping("/restaurant/resend-otp")
    public ResponseEntity<Boolean> resendOtpRestaurant(@RequestBody EmailDto emailDto){

        String email = emailDto.getEmail();

        RestaurantRegistration restaurantRegistration = restaurantInitialService.findByUserEmail(email);
        boolean resentOtpStatus = otpService.resendOtpUsingRestaurantRegistrationId(restaurantRegistration);

        if(!resentOtpStatus){
            return new ResponseEntity<>(false,HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<>(true, HttpStatus.ACCEPTED);
    }




}
