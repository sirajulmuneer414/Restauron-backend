package dev.siraj.restauron.repository.otpRepository;

import dev.siraj.restauron.entity.otpRegistration.OtpAndUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpAndUserRepository extends JpaRepository<OtpAndUser,Long> {

    OtpAndUser findByUserEmail(String userEmail);
}
