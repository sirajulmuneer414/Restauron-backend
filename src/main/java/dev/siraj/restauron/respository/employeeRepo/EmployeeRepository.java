package dev.siraj.restauron.respository.employeeRepo;

import dev.siraj.restauron.entity.users.Employee;
import dev.siraj.restauron.entity.users.UserAll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    Employee findByUser(UserAll user);

    void deleteByUser(UserAll user);
}
