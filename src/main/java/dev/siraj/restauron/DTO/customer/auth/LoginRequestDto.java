package dev.siraj.restauron.DTO.customer.auth;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String email;
    private String password;

}
