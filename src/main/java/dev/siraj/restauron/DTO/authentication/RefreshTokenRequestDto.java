package dev.siraj.restauron.DTO.authentication;

import lombok.Data;

@Data
public class RefreshTokenRequestDto {
    private String oldRefreshToken;
}
