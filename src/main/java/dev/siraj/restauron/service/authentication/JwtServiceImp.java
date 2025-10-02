package dev.siraj.restauron.service.authentication;


import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.respository.userRepo.UserRepository;
import dev.siraj.restauron.service.authentication.interfaces.JwtService;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

@Service
public class JwtServiceImp implements JwtService {

    @Autowired
    private IdEncryptionService idEncryptionService;

    @Autowired
    private UserRepository userRepository;

    @Value("${application.security.jwt.secret-key}")
    private String secret_key;

    @Value("${application.security.jwt.expiration}")
    private long expiration;

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
        return buildToken(role, username, name, userId);

    }

    private String buildToken( String role,
                               String username,
                               String name,
                               Long userId
    ){

        UserAll user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));

        return Jwts.builder()
                .claim("role",role)
                .setSubject(username)
                .claim("userId", idEncryptionService.encryptLongId(userId))
                .claim("username",name)
                .claim("email",username)
                .claim("status",user.getStatus())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
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


    private byte[] getSigningKey() {
        System.out.println("secret key : "+secret_key);
        System.out.println("expiration : "+expiration);
        return Decoders.BASE64.decode(secret_key);

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
