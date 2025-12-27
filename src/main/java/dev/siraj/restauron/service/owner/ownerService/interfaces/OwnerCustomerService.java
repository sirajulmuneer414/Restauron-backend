package dev.siraj.restauron.service.owner.ownerService.interfaces;

import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.DTO.owner.customerManagement.CustomerDetailDto;
import dev.siraj.restauron.DTO.owner.customerManagement.CustomerResponseDto;
import dev.siraj.restauron.DTO.owner.customerManagement.UpdateCustomerDto;
import dev.siraj.restauron.DTO.owner.customerManagement.UpdateStatusDto;
import dev.siraj.restauron.DTO.owner.orderManagement.CustomerSearchResultDto;
import dev.siraj.restauron.entity.enums.AccountStatus;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface OwnerCustomerService {
    Page<CustomerResponseDto> findCustomersByRestaurant(String restaurantEncryptedId, PageRequestDto filter);
    CustomerDetailDto findCustomerDetails(String encryptedCustomerId);
    void updateCustomerStatus(String encryptedCustomerId, UpdateStatusDto statusDto);
    void updateCustomerDetails(String encryptedCustomerId, UpdateCustomerDto updateDto);
    void deleteCustomer(String encryptedCustomerId);

    void approveUnblockRequest(String requestEncryptedId);

    void rejectUnblockRequest(String requestEncryptedId, String ownerResponse);

    CustomerSearchResultDto findCustomerForOwner(String encryptedRestaurantId, String phone, String email);
}
