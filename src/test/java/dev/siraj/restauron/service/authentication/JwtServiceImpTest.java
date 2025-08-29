
package dev.siraj.restauron.service.authentication;

import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JwtServiceImpTest {

    @Mock
    private IdEncryptionService idEncryptionService;

    @InjectMocks
    private JwtServiceImp jwtService;

    private String testToken;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDetails = User.withUsername("testuser").password("password").authorities("USER").build();
        when(idEncryptionService.encryptLongId(1L)).thenReturn("encryptedId");
        testToken = jwtService.generateToken("USER", "testuser", "Test User", 1L);
    }

    @Test
    void testGenerateToken() {
        assertNotNull(testToken);
    }

    @Test
    void testExtractUsername() {
        assertEquals("testuser", jwtService.extractUsername(testToken));
    }

    @Test
    void testExtractUserRole() {
        assertEquals("USER", jwtService.extractUserRole(testToken));
    }

    @Test
    void testIsTokenValid() {
        assertTrue(jwtService.isTokenValid(testToken, userDetails));
    }

    @Test
    void testIsTokenExpired() {
        assertFalse(jwtService.isTokenValid(testToken));
    }

    @Test
    void testIsTokenInvalid_WrongUsername() {
        UserDetails wrongUserDetails = User.withUsername("wronguser").password("password").authorities("USER").build();
        assertFalse(jwtService.isTokenValid(testToken, wrongUserDetails));
    }

    @Test
    void testIsTokenInvalid_Expired() {
        String expiredToken = Jwts.builder()
                .setSubject("testuser")
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(SignatureAlgorithm.HS256, "2CD64d4f4e4b5a6b7c8d9e0f1g2h3i4j5k6l7m8n9o0p1q2r3s4t5u6")
                .compact();
        assertTrue(jwtService.isTokenValid(expiredToken));
    }
}
