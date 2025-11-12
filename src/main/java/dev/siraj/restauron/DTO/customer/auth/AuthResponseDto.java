package dev.siraj.restauron.DTO.customer.auth;

import dev.siraj.restauron.entity.authentication.RefreshToken;
import lombok.Data;

@Data
public class AuthResponseDto
{
    private String token;
    private RefreshToken refreshToken;
    private String specialId;

    public AuthResponseDto(){}
    public AuthResponseDto(String token, RefreshToken refreshToken){this.token = token; this.refreshToken = refreshToken;}
}
