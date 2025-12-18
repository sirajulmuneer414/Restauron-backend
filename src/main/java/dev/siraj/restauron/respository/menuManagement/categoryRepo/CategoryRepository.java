package dev.siraj.restauron.respository.menuManagement.categoryRepo;

import dev.siraj.restauron.entity.enums.AvailabilityStatus;
import dev.siraj.restauron.entity.menuManagement.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {

    Page<Category> findByRestaurantIdAndNameContainingIgnoreCase(Long restaurantId, String name, Pageable pageable);

    Page<Category> findByRestaurantId(Long restaurantId, Pageable pageable);

    List<Category> findByRestaurantIdAndStatus(Long restaurantId, AvailabilityStatus status);

    List<Category> findByRestaurantId(Long restaurantId);
}
