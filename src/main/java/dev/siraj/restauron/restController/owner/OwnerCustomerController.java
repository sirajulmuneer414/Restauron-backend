package dev.siraj.restauron.restController.owner;


import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.DTO.owner.customerManagement.CustomerDetailDto;
import dev.siraj.restauron.DTO.owner.customerManagement.CustomerResponseDto;
import dev.siraj.restauron.DTO.owner.customerManagement.UpdateCustomerDto;
import dev.siraj.restauron.DTO.owner.customerManagement.UpdateStatusDto;
import dev.siraj.restauron.DTO.owner.orderManagement.CustomerSearchResultDto;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.service.ownerService.interfaces.OwnerCustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RolesAllowed(roles = {"OWNER"})
@Slf4j
@RequestMapping("/owner/customer")
public class OwnerCustomerController {

        @Autowired private OwnerCustomerService customerService;

        @PostMapping("/list")
        public ResponseEntity<Page<CustomerResponseDto>> getCustomers(
                @RequestHeader("X-Restaurant-Id") String restaurantEncryptedId,
                @RequestBody PageRequestDto filter) {

            log.info("Received request to fetch customers with filter: {}", filter);
            Page<CustomerResponseDto> page = customerService.findCustomersByRestaurant(restaurantEncryptedId, filter);
            log.info("Successfully fetched the customer list");
            return ResponseEntity.ok(page);
        }


    /**
     * Fetches details for a single customer.
     */
    @GetMapping("/fetch/{customerEncryptedId}")
    public ResponseEntity<CustomerDetailDto> getCustomerDetails(@PathVariable String customerEncryptedId) {
        log.info("Fetching details for customer ID: {}", customerEncryptedId);
        CustomerDetailDto customer = customerService.findCustomerDetails(customerEncryptedId);
        return ResponseEntity.ok(customer);
    }

    /**
     * Updates a customer's status (e.g., to block or unblock them).
     */
    @PatchMapping("/update-status/{customerEncryptedId}")
    public ResponseEntity<Void> updateCustomerStatus(
            @PathVariable String customerEncryptedId,
            @RequestBody UpdateStatusDto statusDto) {
        log.info("Request to update status for customer ID: {} to {}", customerEncryptedId, statusDto.getStatus());
        customerService.updateCustomerStatus(customerEncryptedId, statusDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Updates a customer's personal details.
     */
    @PutMapping("/update-details/{customerEncryptedId}")
    public ResponseEntity<Void> updateCustomerDetails(
            @PathVariable String customerEncryptedId,
            @RequestBody UpdateCustomerDto updateDto) {
        log.info("Request to update details for customer ID: {}", customerEncryptedId);
        customerService.updateCustomerDetails(customerEncryptedId, updateDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Deletes a customer.
     */
    @DeleteMapping("/delete/{customerEncryptedId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String customerEncryptedId) {
        log.warn("Request to delete customer with ID: {}", customerEncryptedId);
        customerService.deleteCustomer(customerEncryptedId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/check")
    public ResponseEntity<CustomerSearchResultDto> checkCustomerExists(
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email) {

        CustomerSearchResultDto customerDto = customerService.findCustomerForOwner(phone, email);

        if(customerDto == null){
            return ResponseEntity.noContent().build();
        }

        return new ResponseEntity<>(customerDto, HttpStatus.OK);

    }


}


