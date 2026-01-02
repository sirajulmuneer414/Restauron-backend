package dev.siraj.restauron.repository.rating;

import dev.siraj.restauron.entity.rating.MenuItemRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRatingRepository extends JpaRepository<MenuItemRating, Long> {

    // Check if customer already rated this menu item for this order
    boolean existsByMenuItemIdAndCustomerIdAndOrderId(Long menuItemId, Long customerId, Long orderId);

    // Get all ratings for a menu item
    List<MenuItemRating> findByMenuItemIdOrderByCreatedAtDesc(Long menuItemId);

    // Get customer's rating for specific item in an order
    Optional<MenuItemRating> findByMenuItemIdAndCustomerIdAndOrderId(Long menuItemId, Long customerId, Long orderId);

    // Calculate average rating for a menu item
    @Query("SELECT AVG(r.rating) FROM MenuItemRating r WHERE r.menuItem.id = :menuItemId")
    Double getAverageRatingByMenuItemId(@Param("menuItemId") Long menuItemId);

    // Count total ratings for a menu item
    Long countByMenuItemId(Long menuItemId);
}