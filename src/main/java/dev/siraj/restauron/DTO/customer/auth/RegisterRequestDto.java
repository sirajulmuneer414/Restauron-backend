package dev.siraj.restauron.DTO.customer.auth;

import lombok.Data;

@Data
public class RegisterRequestDto {
    private String name;
    private String email;
    private String password;

}
