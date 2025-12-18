package dev.siraj.restauron.service.employee.employeeManagementService;

import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.DTO.owner.EmployeeRegistrationRequestDto;
import dev.siraj.restauron.DTO.owner.EmployeeViewDto;
import dev.siraj.restauron.DTO.owner.UpdateEmployeeRequestDto;
import org.springframework.data.domain.Page;

    // This is interface for Employee Management Service

public interface EmployeeManagementService {


    void addEmployee(EmployeeRegistrationRequestDto dto, String encryptedRestaurantId);

    Page<EmployeeViewDto> fetchEmployees(PageRequestDto pageRequestDto, String encryptedRestaurantId);

    EmployeeViewDto getEmployeeDetails(String encryptedId);

    void updateEmployeeDetails(String encryptedId, UpdateEmployeeRequestDto updateDto);

    void deleteEmployee(String encryptedId, String restaurantEncryptedId);
}
