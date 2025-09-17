package dev.siraj.restauron.service.employeeManagementService;

import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.DTO.owner.EmployeeRegistrationRequestDto;
import dev.siraj.restauron.DTO.owner.EmployeeViewDto;
import dev.siraj.restauron.DTO.owner.UpdateEmployeeRequestDto;
import dev.siraj.restauron.entity.enums.AccountStatus;
import dev.siraj.restauron.entity.enums.Roles;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.users.Employee;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.mapping.employee.EmployeeMapping;
import dev.siraj.restauron.respository.employeeRepo.EmployeeRepository;
import dev.siraj.restauron.respository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.respository.userRepo.UserRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import dev.siraj.restauron.service.registrarion.emailService.emailInterface.EmailService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class EmployeeManagementServiceImp implements EmployeeManagementService{

    @Autowired private IdEncryptionService idEncryptionService;
    @Autowired private RestaurantRepository restaurantRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private UserRepository userRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private EmailService emailService;
    @Autowired private EmployeeMapping employeeMapping;

    @Override
    @Transactional
    public void addEmployee(EmployeeRegistrationRequestDto dto, String encryptedRestaurantId) {

        log.info("Starting the process of adding new employee '{}' encryptedRestaurantId '{}'", dto.getName(), encryptedRestaurantId);
        // Decrypt restaurant encrypted id and find the restaurant
        Long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId);

        log.info("Received the Long Id for fetching restaurant: {}", restaurantId);

        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new EntityNotFoundException("Restaurant Not Found for ID: "+restaurantId));
        if(restaurant.getEmployees() == null){
            log.info("Restaurant employee list was empty so added with a new ArrayList<>()");
            restaurant.setEmployeesList(new ArrayList<>());
        }

        log.info("Operating on restaurant: {}", restaurant.getName());

        // Handle potential company email conflicts

        String finalCompanyEmail = findUniqueCompanyEmail(dto.getCompanyEmail());


        // Create and save the generic userAll record
        UserAll user = new UserAll();
        user.setName(dto.getName());
        user.setEmail(finalCompanyEmail);
        user.setRole(Roles.EMPLOYEE);
        user.setPassword(passwordEncoder.encode(dto.getGeneratedPassword()));
        user.setPhone(dto.getPhone());
        user.setStatus(AccountStatus.ACTIVE);

        UserAll savedUser = userRepository.save(user);
        log.info("Successfully saved UserAll record fo {} with ID: {}", savedUser.getName(), savedUser.getId());

        //Create and Save Employee-specific record
        Employee employee = new Employee();
        employee.setUser(savedUser);
        employee.setAdhaarNo(dto.getAdhaarNo());
        employee.setAdhaarPhoto(dto.getAdhaarPhoto());
        employee.setPersonalEmail(dto.getPersonalEmail());

        // By using addEmployee as a helper method add into both restaurant and employee class
        restaurant.addEmployee(employee);

        // as it is cascadeType All only have to save restaurant which persist to employee
        restaurantRepository.save(restaurant);

        log.info("Successfully saved and added employee to restaurant and vice versa for: {}", savedUser.getName());


        // Send the credentials to email
        emailService.sendEmployeeCredentialsToEmail(finalCompanyEmail, dto.getGeneratedPassword(), dto.getPersonalEmail(), restaurant.getName());





    }

    @Override
    public Page<EmployeeViewDto> fetchEmployees(PageRequestDto pageRequestDto, String encryptedRestaurantId) {

        log.info("Fetching employee list for restaurant, Filter: '{}', Search: '{}'", pageRequestDto.getFilter(), pageRequestDto.getSearch());

        // Creating pageable object for pagination
        Pageable pageable = PageRequest.of(pageRequestDto.getPageNo(), pageRequestDto.getSize());

        //Building dynamic query using Specifications
        Specification<Employee> specification = buildSpecification(encryptedRestaurantId, pageRequestDto);

        //Executing the query
        Page<Employee> employeePage = employeeRepository.findAll(specification, pageable);

        log.info("Found {} employees on page {}", employeePage.getNumberOfElements(), pageRequestDto.getPageNo());

        // Mapping entity Page to a Dto Page
        return employeePage.map(employeeMapping::mapToEmployeeViewDto);

    }

    @Override
    public EmployeeViewDto getEmployeeDetails(String encryptedId) {

        Long employeeId = idEncryptionService.decryptToLongId(encryptedId);

        log.info("Decrypted employee ID: {}. Fetching employee...",employeeId);

        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new EntityNotFoundException("Employee not found for employee ID: "+employeeId));

        log.info("Employee found for ID {}. Mapping to DTO", employee.getUser().getName());

        return employeeMapping.mapToEmployeeViewDto(employee);

    }

    @Override
    @Transactional
    public void updateEmployeeDetails(String encryptedId, UpdateEmployeeRequestDto updateDto) {

        Long employeeId = idEncryptionService.decryptToLongId(encryptedId);

        log.info("Attempting to update employee with employee ID: {}",employeeId);

        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new EntityNotFoundException("Employee not found for employee ID: "+employeeId));

        UserAll user = employee.getUser();

        log.info("Updating details for user: {}",user.getName());

        user.setName(updateDto.getName());
        user.setPhone(updateDto.getPhone());

        employee.setPersonalEmail(updateDto.getPersonalEmail());

        employee.setAdhaarNo(updateDto.getAdhaarNo());
        employee.setAdhaarPhoto(updateDto.getAdhaarPhoto());

        employeeRepository.save(employee);

        log.info("Successfully updated employee details for {}", user.getName());

    }

    @Transactional
    @Override
    public void deleteEmployee(String encryptedId) {

        Long employeeId = idEncryptionService.decryptToLongId(encryptedId);

        log.warn("Attempting to delete employee with ID: {}", employeeId);

        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new EntityNotFoundException("Employee not found for the ID : "+ employeeId));


        employeeRepository.delete(employee);

        log.info("Successfully deleted employee and associated user record for employee ID: {} ", employeeId);

    }

    private Specification<Employee> buildSpecification(String encryptedRestaurantId, PageRequestDto pageRequestDto) {

        //Decrypting restaurant ID
        Long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId);

        return (root, query, criteriaBuilder) -> {
            // Root predicate - always filter by the owner's restaurant
            Predicate finalPredicate = criteriaBuilder.equal(root.get("restaurant").get("id"), restaurantId);

            // Adding status filter if provided and not "ALL"
            if(StringUtils.hasText(pageRequestDto.getFilter())){
                try{
                    AccountStatus status =
                            AccountStatus.valueOf(pageRequestDto.getFilter().toUpperCase());

                    Join<Employee, UserAll> userJoin = root.join("user");
                    // Join with userAll to access status
                    finalPredicate = criteriaBuilder.and(finalPredicate, criteriaBuilder.equal(userJoin.get("status"),status));

                }catch (IllegalArgumentException e){
                    log.warn("Invalid status filter provided: {}", pageRequestDto.getFilter());

                }
            }

            // adding searching filter if provided
            if(StringUtils.hasText(pageRequestDto.getSearch())){
                Join<Employee, UserAll> userJoin = root.join("user");


                String searchPattern = "%"+pageRequestDto.getSearch().toLowerCase()+"%";

                Predicate searchPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("name")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("email")),searchPattern)
                );

                finalPredicate = criteriaBuilder.and(finalPredicate, searchPredicate);
            }

            return finalPredicate;
        };
    }

    private String findUniqueCompanyEmail(String companyEmail) {

        log.info("Checking whether the given email {} exists or not", companyEmail);

        if (!userRepository.existsByEmail(companyEmail)) {
            log.info("Base email is unique. Using: {}", companyEmail);
            return companyEmail;
        }

        // if email exist append 1-2 random numbers
        int attempt = 0;

        while(attempt < 100){ // Safety break to prevent an infinite loop

            int randomNumber = ThreadLocalRandom.current().nextInt(1, 100);
            String[] parts = companyEmail.split("@");
            String newEmail = parts[0] + randomNumber + "@" + parts[1];

            if (!userRepository.existsByEmail(newEmail)) {
                log.info("Found unique email after conflict: {}", newEmail);
                return newEmail;
            }
            attempt++;
        }
        log.error("Could not generate a unique email for base: {}", companyEmail);
        throw new IllegalStateException("Unable to generate a unique email for the employee.");

    }
}
