package dev.siraj.restauron.service.registrarion.adminRegistrationService;

import dev.siraj.restauron.entity.restaurant.RestaurantRegistration;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class RegistrationInitialSpecification<T> {

    public Specification<RestaurantRegistration> filterRegistrationInitialAccordingToEnum(String nameOfColumn, T value){


        return((root, query, criteriaBuilder) -> {
            {
                List<Predicate> predicates = new ArrayList<>();

                if (nameOfColumn != null || !nameOfColumn.isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get(nameOfColumn), value));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        });

    }

}

