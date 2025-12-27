package dev.siraj.restauron.restController.general;


import dev.siraj.restauron.DTO.customer.OTP.OtpRequestDto;
import dev.siraj.restauron.DTO.customer.OTP.RegisterVerifyRequestDto;
import dev.siraj.restauron.DTO.customer.auth.AuthResponseDto;
import dev.siraj.restauron.DTO.customer.auth.GoogleLoginRequestDto;
import dev.siraj.restauron.DTO.customer.auth.LoginRequestDto;
import dev.siraj.restauron.entity.authentication.RefreshToken;
import dev.siraj.restauron.service.authentication.interfaces.RefreshTokenService;
import dev.siraj.restauron.service.customer.customerAuthService.CustomerAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/auth")
@Slf4j
public class CustomerAuthController {

     private final CustomerAuthService customerAuthService;
     private final RefreshTokenService refreshTokenService;

     @Autowired
    public CustomerAuthController(CustomerAuthService customerAuthService, RefreshTokenService refreshTokenService) {
        this.customerAuthService = customerAuthService;
        this.refreshTokenService = refreshTokenService;
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginCustomer(
            @RequestBody LoginRequestDto requestDto
            ){
        log.info("Inside the login controller for customer : {}", requestDto.getEmail());

        AuthResponseDto dto = customerAuthService.login(requestDto);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(requestDto.getEmail());

        log.info("completed logging in for the user : {}",requestDto.getEmail());

        dto.setRefreshToken(refreshToken);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponseDto> googleLoginCustomer(
            @RequestBody GoogleLoginRequestDto requestDto,
            @RequestHeader("X-Restaurant-Id") String encryptedId
            ){
        log.info("Inside the google login for customer : {}",requestDto.getToken());

        AuthResponseDto token = customerAuthService.processGoogleLogin(requestDto, encryptedId);


        log.info("Completed logging in for customer through google, token ; {}", requestDto.getToken());
        log.info("The resulting JWT token : {}", token);

        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendRegistrationOtp(@RequestBody OtpRequestDto otpRequest) {
        log.info("Inside otp send for email : {} ", otpRequest.getEmail() );
        customerAuthService.sendRegistrationOtp(otpRequest.getEmail());
        log.info("Otp sent successfully");
        return ResponseEntity.ok("OTP has been sent to your email.");
    }

    // STEP 2: Verify the OTP and complete the registration
    @PostMapping("/register-verify")
    public ResponseEntity<AuthResponseDto> verifyAndRegister(
            @RequestBody RegisterVerifyRequestDto requestDto,
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId) {
        log.info("Inside the register and verify controller for customer : {}", requestDto.getName());
        AuthResponseDto dto = customerAuthService.verifyAndRegister(requestDto, encryptedRestaurantId);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(requestDto.getEmail());

        dto.setRefreshToken(refreshToken);

        log.info("Register and verification completed successfully");
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

}
