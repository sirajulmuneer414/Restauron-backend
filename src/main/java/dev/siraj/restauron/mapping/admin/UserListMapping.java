package dev.siraj.restauron.mapping.admin;

import dev.siraj.restauron.DTO.admin.UserListResponse;
import dev.siraj.restauron.entity.users.Customer;
import dev.siraj.restauron.entity.users.Employee;
import dev.siraj.restauron.entity.users.Owner;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.repository.customerRepo.CustomerRepository;
import dev.siraj.restauron.repository.employeeRepo.EmployeeRepository;
import dev.siraj.restauron.repository.ownerRepo.OwnerRepository;
import dev.siraj.restauron.repository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
/*
    This class is to map UserAll to Dto (request from Admin Side)
 */
@Component
@Slf4j
public class UserListMapping {

    @Autowired
    private IdEncryptionService idEncryptionService;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CustomerRepository customerRepository;


    public Page<UserListResponse> userAllPageToUserResponseDtoPage(Page<UserAll> all) {
        log.info("Inside method to page of pre restaurant to dto for react");

        return all.map(user -> {
            UserListResponse response = new UserListResponse();
            response.setEncryptedId(idEncryptionService.encryptLongId(user.getId()));
            response.setName(user.getName());
            response.setStatus(user.getStatus().name());
            response.setRole(user.getRole().name());
            response.setEmail(user.getEmail());

            switch (user.getRole().name()){
                case "OWNER" :
                    Owner owner = ownerRepository.findByUser(user);
                    response.setRestaurantName(restaurantRepository.findByOwner(owner).getName());
                    break;
                case "EMPLOYEE" :
                    Employee employee = employeeRepository.findByUser(user).orElseThrow(() -> new EntityNotFoundException("Employee not found"));
                    response.setRestaurantName(employee.getRestaurant().getName());
                    break;
                case "CUSTOMER" :
                    Customer customer = customerRepository.findByUser(user);
                    response.setRestaurantName(customer.getRestaurant().getName());
                    break;
                default:
                    response.setRestaurantName("N/A");
            }

            return response;
        });
    }

}
