package dev.siraj.restauron.respository.blockAndUnblockRepo;

import dev.siraj.restauron.entity.blockAndUnblock.CustomerBlock;
import dev.siraj.restauron.entity.users.UserAll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerBlockRepository extends JpaRepository<CustomerBlock,Long> {

    Optional<CustomerBlock> findByUser(UserAll user);
}
