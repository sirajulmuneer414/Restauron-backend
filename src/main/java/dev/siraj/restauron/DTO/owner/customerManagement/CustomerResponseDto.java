package dev.siraj.restauron.DTO.owner.customerManagement;

import dev.siraj.restauron.DTO.customer.blockInfo.UnblockRequestDetailsDto;
import dev.siraj.restauron.entity.enums.AccountStatus;
import lombok.Data;

import java.util.List;

@Data
public class CustomerResponseDto {
    private String encryptedId;
    private String name;
    private String email;
    private String phone;
    private AccountStatus status;
    private String profilePictureUrl;

    private String blockSubject;
    private String blockDescription;
    private boolean isBlocked;

    private List<UnblockRequestDetailsDto> unblockRequests;
}