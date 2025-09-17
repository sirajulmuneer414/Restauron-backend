package dev.siraj.restauron.DTO.owner;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class EmployeeViewDto {
    private String name;

    private String email;

    private String personalEmail;

    private String status;

    private String encryptedId;

    private String phone;

    private String adhaarNo;

    private String adhaarPhoto;


}
