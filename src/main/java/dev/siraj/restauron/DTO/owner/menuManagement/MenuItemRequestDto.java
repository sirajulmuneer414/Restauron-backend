package dev.siraj.restauron.DTO.owner.menuManagement;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MenuItemRequestDto {
    private String name;
    private String description;
    private Double price;
    private MultipartFile imageFile;
    private Boolean isVegetarian;
    private String categoryEncryptedId; // Encrypted ID of the category it belongs to
}