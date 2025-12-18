package dev.siraj.restauron.service.employee.employeeService;

import dev.siraj.restauron.DTO.employee.PasswordChangeRequestDto;
import dev.siraj.restauron.DTO.owner.EmployeeUpdateDto;
import dev.siraj.restauron.DTO.owner.EmployeeViewDto;
import dev.siraj.restauron.DTO.owner.RestaurantReduxSettingDto;
import dev.siraj.restauron.DTO.owner.UpdateEmployeeRequestDto;

public interface EmployeeService {
    RestaurantReduxSettingDto findRestaurantByEmployeeFromEncryptedID(String employeeUserId);

    EmployeeViewDto getEmployeeDetailsById(String encryptedId);

    EmployeeViewDto updateEmployeeDetails(String encryptedId, UpdateEmployeeRequestDto updateDto);

    void updateUserPassword(String userEmail, PasswordChangeRequestDto passwordDto);
}
