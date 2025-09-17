package dev.siraj.restauron.DTO.admin;

import lombok.Data;

@Data
public class UserListResponse {

    private String name;

    private String encryptedId;

    private String email;

    private String role;

    private String status;

    private String restaurantName;

    private String phone;


}
