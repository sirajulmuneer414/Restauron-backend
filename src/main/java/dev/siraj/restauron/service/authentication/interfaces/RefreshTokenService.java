package dev.siraj.restauron.service.authentication.interfaces;

import dev.siraj.restauron.entity.authentication.RefreshToken;
import dev.siraj.restauron.entity.users.UserAll;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(String username);

    Optional<RefreshToken> findByToken(String token);

    RefreshToken verifyExpiration(RefreshToken token);

    void deleteByUser(UserAll user);
}
