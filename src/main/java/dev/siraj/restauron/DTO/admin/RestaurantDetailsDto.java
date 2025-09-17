package dev.siraj.restauron.DTO.admin;

import lombok.Data;

@Data
public class RestaurantDetailsDto {
    private String encryptedId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String district;
    private String state;
    private String pincode;
    private String status;

    // Owner Information
    private String ownerName;
    private String ownerEncryptedUserId; // Essential for linking to the user details page
}