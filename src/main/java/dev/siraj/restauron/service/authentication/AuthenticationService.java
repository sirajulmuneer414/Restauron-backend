package dev.siraj.restauron.service.authentication;


import dev.siraj.restauron.DTO.authentication.EmailPasswordDto;
import dev.siraj.restauron.DTO.authentication.JwtAuthResponse;
import dev.siraj.restauron.DTO.authentication.RefreshTokenRequestDto;
import dev.siraj.restauron.entity.authentication.RefreshToken;
import dev.siraj.restauron.entity.enums.Roles;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.respository.userRepo.UserRepository;
import dev.siraj.restauron.service.authentication.interfaces.JwtService;
import dev.siraj.restauron.service.authentication.interfaces.RefreshTokenService;
import dev.siraj.restauron.service.userService.UserServiceInterface.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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


    public JwtAuthResponse createResponseToken(EmailPasswordDto emailPasswordDto){


        Authentication authentication;


        authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(emailPasswordDto.getEmail(), emailPasswordDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserAll user = userService.findUserByEmail(emailPasswordDto.getEmail());

        Roles role = user.getRole();

        String jwtToken = jwtService.generateToken(role.name(),user.getEmail(), user.getName(), user.getId());
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
                    String accessToken = jwtService.generateToken(user.getRole().name(),user.getEmail(),user.getName(),user.getId());
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
}
