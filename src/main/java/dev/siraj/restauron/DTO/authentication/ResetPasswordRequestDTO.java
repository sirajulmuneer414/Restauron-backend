package dev.siraj.restauron.DTO.authentication;

import lombok.Data;

@Data
public class ResetPasswordRequestDTO {

    private String token;
    private String newPassword;

}
