package dev.siraj.restauron.restController.authentication;

import dev.siraj.restauron.DTO.authentication.EmailPasswordDto;
import dev.siraj.restauron.DTO.authentication.JwtAuthResponse;
import dev.siraj.restauron.DTO.authentication.RefreshTokenRequestDto;
import dev.siraj.restauron.DTO.authentication.ResetPasswordRequestDTO;
import dev.siraj.restauron.service.authentication.AuthenticationService;
import dev.siraj.restauron.service.authentication.interfaces.RefreshTokenService;
import dev.siraj.restauron.service.registrarion.registrationInitialService.registrationInitialInterface.RestaurantInitialService;
import dev.siraj.restauron.service.userService.UserServiceInterface.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private RestaurantInitialService restaurantInitialService;

    @Autowired
    private RefreshTokenService refreshTokenService;


    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> loginUser(@RequestBody EmailPasswordDto emailPasswordDto){

        log.info("Inside the login controller for user with email {}", emailPasswordDto.getEmail());

        boolean doesUserAllExists = userService.userExistsByEmailId(emailPasswordDto.getEmail());

        if(doesUserAllExists){

                JwtAuthResponse response =  authenticationService.createResponseToken(emailPasswordDto);

                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        if(restaurantInitialService.registrationEmailExists(emailPasswordDto.getEmail())){
            return ResponseEntity.accepted().body(null);
        }



        return ResponseEntity.notFound().build();

    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtAuthResponse> refreshToken(@RequestBody RefreshTokenRequestDto request) {

        log.info("Inside the refresh-token controller to fetch refresh token {}", request.getOldRefreshToken());

        JwtAuthResponse dto = authenticationService.recreateAfterRefreshToken(request);

        log.info("successfully created a new token through refresh token {} {}",dto.getNewRefreshToken(), dto.getToken());

        return new ResponseEntity<>(dto,HttpStatus.OK);

    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        try {
            // Extract username from the access token
            String jwt = token.substring(7); // Remove "Bearer "

            authenticationService.logoutUser(jwt);


            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage());
            return ResponseEntity.ok("Logged out"); // Still return success
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        authenticationService.initiatePasswordReset(email);
        return ResponseEntity.ok("Reset link sent to your email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequestDTO request) {

        authenticationService.completePasswordReset(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Password updated successfully.");
    }


}
