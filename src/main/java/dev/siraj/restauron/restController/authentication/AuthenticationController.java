package dev.siraj.restauron.restController.authentication;

import dev.siraj.restauron.DTO.authentication.EmailPasswordDto;
import dev.siraj.restauron.DTO.authentication.JwtAuthResponse;
import dev.siraj.restauron.DTO.authentication.RefreshTokenRequestDto;
import dev.siraj.restauron.DTO.customer.auth.AuthResponseDto;
import dev.siraj.restauron.entity.authentication.RefreshToken;
import dev.siraj.restauron.entity.users.UserAll;
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
    public ResponseEntity<?> loginUser(@RequestBody EmailPasswordDto emailPasswordDto){

        System.out.println("Inside LogInUser");

        boolean doesUserAllExists = userService.userExistsByEmailId(emailPasswordDto.getEmail());

        if(doesUserAllExists){
            System.out.println("User does exists");

                JwtAuthResponse response =  authenticationService.createResponseToken(emailPasswordDto);

                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        if(restaurantInitialService.registrationEmailExists(emailPasswordDto.getEmail())){
            return new ResponseEntity<>("Please Wait Till your restaurant account is approved", HttpStatus.ACCEPTED);
        }



        return new ResponseEntity<>("Bad Credentials Please Check",HttpStatus.NOT_FOUND);

    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtAuthResponse> refreshToken(@RequestBody RefreshTokenRequestDto request) {
        JwtAuthResponse dto = authenticationService.recreateAfterRefreshToken(request);

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

}
