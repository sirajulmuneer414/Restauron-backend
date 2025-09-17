package dev.siraj.restauron.DTO.employee;

import lombok.Data;

@Data
public class PasswordChangeRequestDto {
    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;
}
