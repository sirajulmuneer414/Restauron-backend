package dev.siraj.restauron.specification;

import dev.siraj.restauron.entity.enums.Roles;

import dev.siraj.restauron.entity.users.UserAll;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

        public static Specification<UserAll> withDynamicQuery(String filter, String search) {
            return (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();

                // Always exclude ADMIN role
                predicates.add(criteriaBuilder.notEqual(root.get("role"), Roles.ADMIN));

                // Add filter by role if provided
                if (filter != null && !filter.equalsIgnoreCase("ALL")) {
                    try {
                        Roles role = Roles.valueOf(filter.toUpperCase());
                        predicates.add(criteriaBuilder.equal(root.get("role"), role));
                    } catch (IllegalArgumentException e) {
                        // Handle invalid role string if necessary, or ignore
                    }
                }

                // Add search by name or email if provided
                if (search != null && !search.trim().isEmpty()) {
                    String likePattern = "%" + search.toLowerCase() + "%";
                    Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern);
                    Predicate emailPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern);
                    predicates.add(criteriaBuilder.or(namePredicate, emailPredicate));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };

        }
    }



