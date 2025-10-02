package dev.siraj.restauron.respository.menuManagement.categoryRepo;

import dev.siraj.restauron.entity.enums.ItemStatus;
import dev.siraj.restauron.entity.menuManagement.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {

    Page<Category> findByRestaurantIdAndNameContainingIgnoreCase(Long restaurantId, String name, Pageable pageable);

    Page<Category> findByRestaurantId(Long restaurantId, Pageable pageable);

    List<Category> findByRestaurantIdAndStatus(Long restaurantId, ItemStatus status);
}
