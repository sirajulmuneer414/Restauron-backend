package dev.siraj.restauron.service.employeeManagementService;

import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.DTO.owner.EmployeeRegistrationRequestDto;
import dev.siraj.restauron.DTO.owner.EmployeeViewDto;
import org.springframework.data.domain.Page;

public interface EmployeeManagementService {


    void addEmployee(EmployeeRegistrationRequestDto dto, String encryptedRestaurantId);

    Page<EmployeeViewDto> fetchEmployees(PageRequestDto pageRequestDto, String encryptedRestaurantId);
}
