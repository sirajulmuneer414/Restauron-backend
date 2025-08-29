package dev.siraj.restauron.DTO.owner;

import lombok.Data;

@Data
public class EmployeeRegistrationRequestDto {

    private String name;
    private String personalEmail;
    private String phone;
    private String adhaarNo;
    private String adhaarPhoto;
    private String companyEmail;
    private String generatedPassword;

}
