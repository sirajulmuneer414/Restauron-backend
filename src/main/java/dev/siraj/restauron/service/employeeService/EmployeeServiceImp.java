package dev.siraj.restauron.service.employeeService;

import dev.siraj.restauron.DTO.employee.PasswordChangeRequestDto;
import dev.siraj.restauron.DTO.owner.EmployeeUpdateDto;
import dev.siraj.restauron.DTO.owner.EmployeeViewDto;
import dev.siraj.restauron.DTO.owner.RestaurantReduxSettingDto;
import dev.siraj.restauron.DTO.owner.UpdateEmployeeRequestDto;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.users.Employee;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.mapping.employee.EmployeeMapping;
import dev.siraj.restauron.respository.employeeRepo.EmployeeRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import dev.siraj.restauron.service.userService.UserServiceInterface.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class EmployeeServiceImp implements EmployeeService{

    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private IdEncryptionService idEncryptionService;
    @Autowired private UserService userService;
    @Autowired private EmployeeMapping employeeMapping;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public RestaurantReduxSettingDto findRestaurantByEmployeeFromEncryptedID(String employeeUserId) {

        Long userId = idEncryptionService.decryptToLongId(employeeUserId);

        UserAll user = userService.findUserById(userId);

        Employee employee = employeeRepository.findByUser(user);

        Restaurant restaurant = employee.getRestaurant();

        RestaurantReduxSettingDto dto = new RestaurantReduxSettingDto();

        dto.setRestaurantName(restaurant.getName());
        dto.setRestaurantEncryptedId(idEncryptionService.encryptLongId(restaurant.getId()));
        dto.setSpecialId(idEncryptionService.encryptLongId(employee.getId()));

        return dto;
    }

    @Override
    public EmployeeViewDto getEmployeeDetailsById(String encryptedId) {

        Long id = idEncryptionService.decryptToLongId(encryptedId);

        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Employee not found for employee ID: "+id));

        return employeeMapping.mapToEmployeeViewDto(employee);
    }

    @Override
    @Transactional
    public EmployeeViewDto updateEmployeeDetails(String encryptedId, UpdateEmployeeRequestDto updateDto) {

        Long id = idEncryptionService.decryptToLongId(encryptedId);

        log.info("Attempting to update employee with ID : {}",id);

        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Employee not found for employee ID: "+id));

        UserAll user = employee.getUser();

        log.info("Updating details for user: {}",user.getName());

        user.setName(updateDto.getName());
        user.setPhone(updateDto.getPhone());

        employee.setPersonalEmail(updateDto.getPersonalEmail());

        employeeRepository.save(employee);

        log.info("Successfully updated employee details for {}", user.getName());

        return employeeMapping.mapToEmployeeViewDto(employee);
    }

    @Override
    @Transactional
    public void updateUserPassword(String userEmail, PasswordChangeRequestDto passwordDto) {

        log.info("Starting password update process for user: {}", userEmail);

        UserAll user = userService.findUserByEmail(userEmail);

        if(user == null){
            throw new EntityNotFoundException("User not found with email: "+userEmail);
        }

        log.info("Found user {} for password update.", user.getName());

        if(!passwordEncoder.matches(passwordDto.getCurrentPassword(), user.getPassword())){
            log.warn("Incorrect current password attempt for user: {}", userEmail);
            throw new IllegalArgumentException("The current password you entered is incorrect.");
        }

        if(!passwordDto.getNewPassword().equals(passwordDto.getConfirmNewPassword())) {
            log.warn("New password and confirmation did not match for user: {}", userEmail);
            throw new IllegalArgumentException("The new passwords do not match.");
        }

        if (passwordDto.getNewPassword().length() < 8) {
            throw new IllegalArgumentException("The new password must be at least 8 characters long.");
        }

        // 5. Check if new password is different from current password
        if (passwordEncoder.matches(passwordDto.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("The new password must be different from your current password.");
        }

        // 6. Encode the new password and save
        String encodedNewPassword = passwordEncoder.encode(passwordDto.getNewPassword());
        user.setPassword(encodedNewPassword);


        userService.save(user);
        log.info("Successfully updated password for user: {}", userEmail);


    }



}
