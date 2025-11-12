package dev.siraj.restauron.DTO.owner.orderManagement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSearchResultDto {
    private String encryptedId;
    private String name;
    private String phone;
    private String email;
}