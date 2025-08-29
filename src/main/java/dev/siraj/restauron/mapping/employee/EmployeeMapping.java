package dev.siraj.restauron.mapping.employee;

import dev.siraj.restauron.DTO.owner.EmployeeViewDto;
import dev.siraj.restauron.entity.users.Employee;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapping {

    @Autowired
    private IdEncryptionService idEncryptionService;


    public EmployeeViewDto mapToEmployeeViewDto(Employee employee){
        UserAll user = employee.getUser();
        EmployeeViewDto dto = new EmployeeViewDto();

        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setStatus(user.getStatus().name());

        dto.setEncryptedId(idEncryptionService.encryptLongId(employee.getId()));

        dto.setAdhaarNo(employee.getAdhaarNo());
        dto.setAdhaarPhoto(employee.getAdhaarPhoto());

        return dto;
    }

}
