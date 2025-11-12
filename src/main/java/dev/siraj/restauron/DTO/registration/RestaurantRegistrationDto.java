package dev.siraj.restauron.DTO.registration;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RestaurantRegistrationDto {

    private String restaurantName;

    private String name;

    private String email;

    private String restaurantEmail;

    private String restaurantPhone;

    private String phone;

    private String password;

    private String aadhaarNumber;

    private MultipartFile aadhaarPhoto;

    private String restaurantAddress;

    private String district;

    private String state;

    private String pincode;

}
