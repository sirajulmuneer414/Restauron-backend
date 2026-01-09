package dev.siraj.restauron.service.authentication.interfaces;


import dev.siraj.restauron.entity.enums.AccessLevelStatus;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.function.Function;

// jwt service interface
public interface JwtService {
    public String extractUsername(String token);
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    public String generateToken(
            String role,
            String username,
            String name,
            Long userId,
            AccessLevelStatus accessLevelStatus
    );

    public boolean isTokenValid(String token, UserDetails userDetails);

    public boolean isTokenValid(String token);

    public String generateToken(Authentication authentication);

    String extractUserRole(String token);
}


