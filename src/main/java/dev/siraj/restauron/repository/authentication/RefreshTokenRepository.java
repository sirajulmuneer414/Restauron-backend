package dev.siraj.restauron.repository.authentication;

import dev.siraj.restauron.entity.authentication.RefreshToken;
import dev.siraj.restauron.entity.users.UserAll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository <RefreshToken , Long> {
    Optional <RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(UserAll user);

    void deleteByUser(UserAll user);
}