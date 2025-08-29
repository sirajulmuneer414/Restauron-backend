package dev.siraj.restauron.restController.authentication;

import dev.siraj.restauron.DTO.authentication.EmailPasswordDto;
import dev.siraj.restauron.service.authentication.AuthenticationService;
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



    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody EmailPasswordDto emailPasswordDto){

        System.out.println("Inside LogInUser");

        boolean doesUserAllExists = userService.userExistsByEmailId(emailPasswordDto.getEmail());

        if(doesUserAllExists){
            System.out.println("User does exists");

                return authenticationService.createResponseToken(emailPasswordDto);
        }

        if(restaurantInitialService.registrationEmailExists(emailPasswordDto.getEmail())){
            return new ResponseEntity<>("Please Wait Till your restaurant account is approved", HttpStatus.ACCEPTED);
        }



        return new ResponseEntity<>("Bad Credentials Please Check",HttpStatus.NOT_FOUND);


    }
}
