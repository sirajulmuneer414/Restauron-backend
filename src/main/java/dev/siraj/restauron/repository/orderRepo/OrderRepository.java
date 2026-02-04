package dev.siraj.restauron.repository.orderRepo;

import dev.siraj.restauron.entity.enums.OrderStatus;
import dev.siraj.restauron.entity.orderManagement.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    @Query("SELECT o FROM Order o ORDER BY o.id DESC LIMIT 1")
    Optional<Order> findLastOrder();


    @Query("SELECT COALESCE(SUM(o.totalAmount), 0.0) FROM Order o WHERE o.status = 'COMPLETED' AND o.orderDate >= :startDate AND o.restaurant.id = :restaurantId")
    Double findTotalSalesSince(@Param("startDate") LocalDate startDate, @Param("restaurantId") Long restaurantId);


    @Query(
            value = """
            SELECT
                mi.id              AS menu_item_id,
                mi.name            AS item_name,
                COUNT(DISTINCT o.id) AS total_orders,
                SUM(oi.quantity * oi.price_at_order) AS total_revenue
            FROM restaurant_orders o
            JOIN order_items oi ON oi.order_id = o.id
            JOIN menu_item mi    ON oi.menu_item_id = mi.id
            WHERE o.restaurant_id = :restaurantId
              AND o.status = 'COMPLETED'
            GROUP BY mi.id, mi.name
            ORDER BY total_revenue DESC
            LIMIT :limit
            """,
            nativeQuery = true
    )
    List<Object[]> findTopSellingItemsByRestaurant(
            @Param("restaurantId") Long restaurantId,
            @Param("limit") int limit
    );

    List<Order> findTop5ByRestaurantIdOrderByOrderDateDesc(long restaurantId);

    List<Order> findByCustomerIdAndStatusIn(Long customerId, List<OrderStatus> pending);

    List<Order> findByCustomerIdAndStatus(Long customerId, OrderStatus orderStatus);

    List<Order> findByCustomerIdOrderByOrderDateDescOrderTimeDesc(Long customerId);


    List<Order> findAllByRestaurantIdAndStatusAndOrderDateBetween(
            Long restaurantId,
            OrderStatus status,
            LocalDate startDate,
            LocalDate endDate
    );
}