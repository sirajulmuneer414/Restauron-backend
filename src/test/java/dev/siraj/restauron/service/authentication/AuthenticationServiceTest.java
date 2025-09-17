
package dev.siraj.restauron.service.authentication;

import dev.siraj.restauron.DTO.authentication.EmailPasswordDto;
import dev.siraj.restauron.DTO.authentication.JwtAuthResponse;
import dev.siraj.restauron.entity.enums.Roles;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.service.authentication.interfaces.JwtService;
import dev.siraj.restauron.service.userService.UserServiceInterface.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateResponseToken_Success() {
        EmailPasswordDto emailPasswordDto = new EmailPasswordDto("test@test.com", "password");
        UserAll user = mock(UserAll.class);
        when(user.getEmail()).thenReturn("test@test.com");
        when(user.getName()).thenReturn("Test User");
        when(user.getRole()).thenReturn(Roles.CUSTOMER);
        when(user.getId()).thenReturn(1L);

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userService.findUserByEmail(emailPasswordDto.getEmail())).thenReturn(user);
        when(jwtService.generateToken(anyString(), anyString(), anyString(), anyLong())).thenReturn("test_token");

        ResponseEntity<?> response = authenticationService.createResponseToken(emailPasswordDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof JwtAuthResponse);
        assertEquals("test_token", ((JwtAuthResponse) response.getBody()).getToken());
    }

    @Test
    void testCreateResponseToken_AuthenticationFailure() {
        EmailPasswordDto emailPasswordDto = new EmailPasswordDto("test@test.com", "password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Bad credentials") {});

        ResponseEntity<?> response = authenticationService.createResponseToken(emailPasswordDto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Bad credentials", response.getBody());
    }
}
