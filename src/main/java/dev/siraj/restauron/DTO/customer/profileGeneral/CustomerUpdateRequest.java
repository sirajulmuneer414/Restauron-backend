package dev.siraj.restauron.DTO.customer.profileGeneral;

import dev.siraj.restauron.entity.enums.AccountStatus;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CustomerUpdateRequest {
    private String name;
    private String email;
    private String phone;
    private String profilePictureUrl;
    private MultipartFile profilePicture;
}
