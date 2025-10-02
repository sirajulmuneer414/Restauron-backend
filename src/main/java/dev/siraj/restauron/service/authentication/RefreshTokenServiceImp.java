package dev.siraj.restauron.service.authentication;

import dev.siraj.restauron.entity.authentication.RefreshToken;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.respository.authentication.RefreshTokenRepository;
import dev.siraj.restauron.respository.userRepo.UserRepository;
import dev.siraj.restauron.service.authentication.interfaces.RefreshTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class RefreshTokenServiceImp implements RefreshTokenService {

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long REFRESH_EXPIRATION;

    @Autowired
    private RefreshTokenRepository  refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public RefreshToken createRefreshToken(String username) {

        UserAll user = userRepository.findByEmail(username);

        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);
        if (existingToken.isPresent()) {
            log.info("Deleting existing refresh token for user: {}", username);
            refreshTokenRepository.delete(existingToken.get());
            refreshTokenRepository.flush(); // Ensure deletion is committed
        }
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.findByEmail(username));
        refreshToken.setExpiryDate(Instant.now().plusMillis(REFRESH_EXPIRATION));
        refreshToken.setToken(UUID.randomUUID().toString());
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request.");
        }
        return token;
    }

    @Override
    public void deleteByUser(UserAll user) {
            refreshTokenRepository.deleteByUser(user);
    }
}
