package dev.siraj.restauron.service.authentication;


import dev.siraj.restauron.DTO.authentication.EmailPasswordDto;
import dev.siraj.restauron.DTO.authentication.JwtAuthResponse;
import dev.siraj.restauron.entity.enums.Roles;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.service.authentication.interfaces.JwtService;
import dev.siraj.restauron.service.userService.UserServiceInterface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;


    public ResponseEntity<?> createResponseToken(EmailPasswordDto emailPasswordDto){


        Authentication authentication;

        try {
             authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(emailPasswordDto.getEmail(), emailPasswordDto.getPassword()));

        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserAll user = userService.findUserByEmail(emailPasswordDto.getEmail());

        Roles role = user.getRole();

        String jwtToken = jwtService.generateToken(role.name(),user.getEmail(), user.getName(), user.getId());

        System.out.println("Reached till the return");

        return new ResponseEntity<>(new JwtAuthResponse(jwtToken),HttpStatus.OK);
    }


}
