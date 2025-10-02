package dev.siraj.restauron.DTO.owner.customerManagement;


import dev.siraj.restauron.entity.enums.AccountStatus;
import lombok.Data;

@Data
public class UpdateStatusDto {
    private AccountStatus status;

    private String subject;

    private String description;
}