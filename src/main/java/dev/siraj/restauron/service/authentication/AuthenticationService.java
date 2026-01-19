package dev.siraj.restauron.service.authentication;


import dev.siraj.restauron.DTO.authentication.EmailPasswordDto;
import dev.siraj.restauron.DTO.authentication.JwtAuthResponse;
import dev.siraj.restauron.DTO.authentication.RefreshTokenRequestDto;
import dev.siraj.restauron.entity.authentication.PasswordResetToken;
import dev.siraj.restauron.entity.authentication.RefreshToken;
import dev.siraj.restauron.entity.enums.AccessLevelStatus;
import dev.siraj.restauron.entity.enums.Roles;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.repository.authentication.PasswordResetTokenRepository;
import dev.siraj.restauron.repository.employeeRepo.EmployeeRepository;
import dev.siraj.restauron.repository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.repository.userRepo.UserRepository;
import dev.siraj.restauron.service.authentication.interfaces.JwtService;
import dev.siraj.restauron.service.authentication.interfaces.RefreshTokenService;
import dev.siraj.restauron.service.registrarion.emailService.emailInterface.EmailService;
import dev.siraj.restauron.service.userService.UserServiceInterface.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class AuthenticationService {


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private EmployeeRepository employeeRepository;


    public JwtAuthResponse createResponseToken(EmailPasswordDto emailPasswordDto){


        Authentication authentication;


        authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(emailPasswordDto.getEmail(), emailPasswordDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserAll user = userService.findUserByEmail(emailPasswordDto.getEmail());

        Roles role = user.getRole();

        AccessLevelStatus accessLevelStatus = null;

        String restaurantName = null;

        if(role.equals(Roles.OWNER)){
            Restaurant restaurant = restaurantRepository.findByOwner_User_Id(user.getId()).orElseThrow(() -> new EntityNotFoundException("Restaurant not found for owner"));
            if(restaurant.getAccessLevel() == null){
                restaurant.setAccessLevel(AccessLevelStatus.FULL);
                accessLevelStatus = AccessLevelStatus.FULL;
                restaurantName = restaurant.getName();
                restaurantRepository.save(restaurant);
            }
            else {
                accessLevelStatus = restaurant.getAccessLevel();
                restaurantName = restaurant.getName();
            }
        } else if (role.equals(Roles.EMPLOYEE)) {
            Restaurant restaurant = employeeRepository.findByUser(user)
                    .orElseThrow(() -> new EntityNotFoundException("Employee not found"))
                    .getRestaurant();

            if(restaurant.getAccessLevel() == null){
                restaurant.setAccessLevel(AccessLevelStatus.FULL);
                accessLevelStatus = AccessLevelStatus.FULL;
                restaurantName = restaurant.getName();
                restaurantRepository.save(restaurant);
            } else {
                accessLevelStatus = restaurant.getAccessLevel();
                restaurantName = restaurant.getName();
            }

        }

        String jwtToken = jwtService.generateToken(role.name(),user.getEmail(), user.getName(), user.getId(), accessLevelStatus, restaurantName);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        System.out.println("Reached till the return");

        return new JwtAuthResponse(jwtToken, refreshToken.getToken());
    }

    public JwtAuthResponse recreateAfterRefreshToken(RefreshTokenRequestDto requestDto){

        log.info("Inside the refreshTokenRecreational service");

        return refreshTokenService.findByToken(requestDto.getOldRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    Restaurant restaurant = restaurantRepository.findByOwner_User_Id(user.getId()).orElse(null);
                    String accessToken = jwtService.generateToken(user.getRole().name(),user.getEmail(),user.getName(),user.getId(),
                            restaurant != null ? restaurant.getAccessLevel() : null, restaurant != null ? restaurant.getName() : null);
                    return new JwtAuthResponse(accessToken,requestDto.getOldRefreshToken());
                }).orElseThrow(() -> new RuntimeException("Refresh token not found"));

    }


    @Transactional
    public void logoutUser(String jwt) {

        String username = jwtService.extractUsername(jwt);

        // Find user and delete their refresh token
        UserAll user = userRepository.findByEmail(username);
        if (user != null) {
            refreshTokenService.deleteByUser(user);
        }
    }


    @Transactional
    public void initiatePasswordReset(String email) {
        UserAll user = userRepository.findByEmail(email);

                if(user == null) {
                    throw new EntityNotFoundException("User not found with email: " + email);
                }
        // Generate UUID Token
        String token = UUID.randomUUID().toString();

        // Save Token (Delete old ones first to keep DB clean)
        tokenRepository.deleteByEmail(email);
        PasswordResetToken resetToken = new PasswordResetToken(email, token);
        tokenRepository.save(resetToken);

        // Send Email (Mock implementation for dev)


        String link = "http://localhost:5173/reset-password?token=" + token;
        System.out.println("RESET LINK: " + link); // FOR DEV ONLY

        emailService.sendPasswordResetEmail(user.getEmail(), link, user.getName());


        // emailService.sendSimpleMessage(email, "Password Reset", "Click here: " + link);
    }

    // Password Encoder Bean
    @Transactional
    public void completePasswordReset(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("Token expired");
        }

        // Update User Password
        UserAll user = userRepository.findByEmail(resetToken.getEmail());

        if(user == null) {
            throw new EntityNotFoundException("User not found with email: " + resetToken.getEmail());
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Delete used token
        tokenRepository.delete(resetToken);
    }

}
