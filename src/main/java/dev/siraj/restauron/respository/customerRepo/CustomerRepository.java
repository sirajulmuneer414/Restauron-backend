package dev.siraj.restauron.respository.customerRepo;

import dev.siraj.restauron.entity.restaurant.Restaurant;
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


    Optional<Customer> findByUser_Phone(String customerPhone);

    Optional<Customer> findByUser_Email(String email);

    Optional<Customer> findByUser_PhoneAndRestaurant(String phone, Restaurant restaurant);

    Optional<Customer> findByUser_EmailAndRestaurant(String email, Restaurant restaurant);

    long countByRestaurantId(Long id);
}
