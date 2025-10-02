package dev.siraj.restauron.service.ownerService.interfaces;

import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.DTO.owner.customerManagement.CustomerDetailDto;
import dev.siraj.restauron.DTO.owner.customerManagement.CustomerResponseDto;
import dev.siraj.restauron.DTO.owner.customerManagement.UpdateCustomerDto;
import dev.siraj.restauron.DTO.owner.customerManagement.UpdateStatusDto;
import dev.siraj.restauron.entity.enums.AccountStatus;
import org.springframework.data.domain.Page;

public interface OwnerCustomerService {
    Page<CustomerResponseDto> findCustomersByRestaurant(String restaurantEncryptedId, PageRequestDto filter);
    CustomerDetailDto findCustomerDetails(String encryptedCustomerId);
    void updateCustomerStatus(String encryptedCustomerId, UpdateStatusDto statusDto);
    void updateCustomerDetails(String encryptedCustomerId, UpdateCustomerDto updateDto);
    void deleteCustomer(String encryptedCustomerId);

    void approveUnblockRequest(String requestEncryptedId);

    void rejectUnblockRequest(String requestEncryptedId, String ownerResponse);
}
