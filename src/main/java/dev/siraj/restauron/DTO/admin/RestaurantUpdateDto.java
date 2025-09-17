package dev.siraj.restauron.DTO.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RestaurantUpdateDto {
    @NotBlank private String name;
    @NotBlank private String email;
    @NotBlank private String phone;
    private String address;
    private String district;
    private String state;
    private String pincode;
}