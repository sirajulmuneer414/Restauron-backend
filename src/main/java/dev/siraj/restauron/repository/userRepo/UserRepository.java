package dev.siraj.restauron.repository.userRepo;

import dev.siraj.restauron.entity.enums.Roles;
import dev.siraj.restauron.entity.users.UserAll;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

        // Repository for user data access
@Repository
public interface UserRepository extends JpaRepository<UserAll, Long>, JpaSpecificationExecutor<UserAll> {
    UserAll findByEmail(String email);

    boolean existsByEmail(String email);

    Page<UserAll> findByRole(Roles roles, Pageable pageable);
    Page<UserAll> findByRoleNot(Roles roles, Pageable pageable);

}
