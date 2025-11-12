package dev.siraj.restauron.restController.customer.profileControllers;


import dev.siraj.restauron.DTO.customer.profileGeneral.CustomerStatusDto;
import dev.siraj.restauron.DTO.customer.profileGeneral.CustomerUpdateRequest;
import dev.siraj.restauron.DTO.owner.customerManagement.CustomerResponseDto;
import dev.siraj.restauron.entity.users.Customer;
import dev.siraj.restauron.service.customer.profileServices.CustomerProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/profile")
public class CustomerProfileGeneralController {


    @Autowired
    private CustomerProfileService customerProfileService;


    @GetMapping("/status/{userId}")
    public ResponseEntity <CustomerStatusDto> getMyStatusForRestaurant(
            @RequestHeader("X-Restaurant-Id") String restaurantEncryptedId,
            @PathVariable("userId") String userId) {


        CustomerStatusDto statusDto = customerProfileService.getCustomerStatusForRestaurant(userId, restaurantEncryptedId);
        return ResponseEntity.ok(statusDto);
    }


    @GetMapping("/me")
    public ResponseEntity<CustomerResponseDto> getMyProfile(@RequestHeader("X-Customer-Id") String encryptedCustomerId) {

        CustomerResponseDto customer = customerProfileService.getProfile(encryptedCustomerId);

        return ResponseEntity.ok(customer);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateMyProfile(@RequestHeader("X-Customer-Id") String encryptedCustomerId, @RequestBody CustomerUpdateRequest updateRequest) {
        customerProfileService.updateProfile(encryptedCustomerId, updateRequest);
        return ResponseEntity.ok("Profile updated successfully");
    }

    // DELETE /api/customer/profile/delete
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteMyAccount(@RequestHeader("X-Customer-Id") String encryptedCustomerId) {


        customerProfileService.deleteAccount(encryptedCustomerId);
        return ResponseEntity.ok("Account deleted successfully");
    }

    // Example DTO and Mapper to prevent JSON issues

}
