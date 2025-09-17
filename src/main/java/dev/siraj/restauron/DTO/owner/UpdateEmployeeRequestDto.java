package dev.siraj.restauron.DTO.owner;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEmployeeRequestDto {

    private String name;
    private String personalEmail;
    private String phone;
    private String adhaarNo;
    private String adhaarPhoto;
}
