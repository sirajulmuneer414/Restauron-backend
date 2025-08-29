package dev.siraj.restauron.service.authentication;


import dev.siraj.restauron.service.authentication.interfaces.JwtService;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

@Service
public class JwtServiceImp implements JwtService {

    @Autowired
    private IdEncryptionService idEncryptionService;

    private static final String SECRET_KEY = "2CD64d4f4e4b5a6b7c8d9e0f1g2h3i4j5k6l7m8n9o0p1q2r3s4t5u6";

    public String extractUsername(String token) {


        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public String generateToken(
            String role,
            String username,
            String name,
            Long userId
    ) {
        return Jwts.builder()
                .claim("role",role)
                .setSubject(username)
                .claim("userId", idEncryptionService.encryptLongId(userId))
                .claim("username",name)
                .claim("email",username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();

    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    @Override
    public boolean isTokenValid(String token) {
     return isTokenExpired(token);


    }

    private boolean isTokenExpired(String token) {

        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    private String getSigningKey() {
        return SECRET_KEY;

    }

    public String generateToken(Authentication authentication) {
        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();

    }

    @Override
    public String extractUserRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
}
