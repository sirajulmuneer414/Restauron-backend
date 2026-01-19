package dev.siraj.restauron.repository.employeeRepo;

import dev.siraj.restauron.entity.users.Employee;
import dev.siraj.restauron.entity.users.UserAll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repository for employee data access

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {


    Optional<Employee> findByUser(UserAll user);

    void deleteByUser(UserAll user);

    long countByRestaurant_Id(Long restaurantId);
}
