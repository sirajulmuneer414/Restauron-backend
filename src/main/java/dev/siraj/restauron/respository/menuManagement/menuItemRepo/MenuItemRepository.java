package dev.siraj.restauron.respository.menuManagement.menuItemRepo;

import dev.siraj.restauron.entity.enums.ItemStatus;
import dev.siraj.restauron.entity.menuManagement.Category;
import dev.siraj.restauron.entity.menuManagement.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    Page<MenuItem> findByRestaurantIdAndNameContainingIgnoreCase(Long restaurantId, String name, Pageable pageable);

    Page<MenuItem> findByRestaurantId(Long restaurantId, Pageable pageable);

    Page<MenuItem> findByCategoryId(Long categoryId, Pageable pageable);

    Page<MenuItem> findByCategoryIdAndNameContainingIgnoreCase(Long categoryId, String search, Pageable pageable);

    boolean existsByCategoryIdAndStatus(Long id, ItemStatus itemStatus);

    @Modifying
    @Query("UPDATE MenuItem m SET m.status = :newStatus, m.isAvailable = :isAvailable WHERE m.category.id = :categoryId")
    int updateStatusAndAvailabilityByCategoryId(@Param("categoryId") Long categoryId, @Param("newStatus") ItemStatus newStatus, @Param("isAvailable") boolean isAvailable);

    Optional<List<MenuItem>> findByCategory(Category category);
}
