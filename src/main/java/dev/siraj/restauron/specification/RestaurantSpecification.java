package dev.siraj.restauron.specification;


import dev.siraj.restauron.entity.enums.AccountStatus;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RestaurantSpecification {

    public static Specification<Restaurant> withDynamicQuery(String filter, String search) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Filter by Status
            if (filter != null && !filter.equalsIgnoreCase("ALL")) {
                log.info("Inside specification filter setting . {}",filter);
                try {

                    AccountStatus status = AccountStatus.valueOf(filter.toUpperCase());
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                } catch (IllegalArgumentException e) {
                    // Ignore invalid filter values
                }
            }

            // 2. Search by Name, Email, or Owner's Name
            if (search != null && !search.trim().isEmpty()) {
                String likePattern = "%" + search.toLowerCase() + "%";

                // Join to get Owner -> UserAll
                Join<Object, Object> ownerJoin = root.join("owner");
                Join<Object, Object> userJoin = ownerJoin.join("user");

                Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern);
                Predicate emailPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern);
                Predicate ownerNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("name")), likePattern);

                predicates.add(criteriaBuilder.or(namePredicate, emailPredicate, ownerNamePredicate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}