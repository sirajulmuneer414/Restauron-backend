package dev.siraj.restauron.respository.orderRepo;

import dev.siraj.restauron.entity.orderManagement.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    @Query("SELECT o FROM Order o ORDER BY o.id DESC LIMIT 1")
    Optional<Order> findLastOrder();
}