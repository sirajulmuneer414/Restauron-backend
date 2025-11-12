package dev.siraj.restauron.DTO.owner;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class EmployeeRegistrationRequestDto {


    private String name;
    private String personalEmail;
    private String phone;
    private String aadhaarNo;
    private MultipartFile aadhaarImage;
    private String companyEmail;
    private String generatedPassword;

}
