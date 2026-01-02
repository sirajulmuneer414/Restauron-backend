package dev.siraj.restauron.repository.authentication;

import dev.siraj.restauron.entity.authentication.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// PasswordResetTokenRepository for CRUD operations on PasswordResetToken entity

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    void deleteByEmail(String email);

    Optional<PasswordResetToken> findByToken(String token);
}
