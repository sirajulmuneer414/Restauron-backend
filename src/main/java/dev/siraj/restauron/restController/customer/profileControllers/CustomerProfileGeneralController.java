package dev.siraj.restauron.restController.customer.profileControllers;


import dev.siraj.restauron.DTO.customer.profileGeneral.CustomerStatusDto;
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
}
