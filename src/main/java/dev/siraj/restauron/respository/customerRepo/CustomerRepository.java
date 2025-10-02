package dev.siraj.restauron.respository.customerRepo;

import dev.siraj.restauron.entity.users.Customer;
import dev.siraj.restauron.entity.users.UserAll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long>, JpaSpecificationExecutor<Customer>{

    Customer findByUser(UserAll user);

    void deleteByUser(UserAll user);

    Optional<Customer> findByUserIdAndRestaurantId(Long customerEmail, Long restaurantId);
}
