package dev.siraj.restauron.restController.customer.blockControllers;


import dev.siraj.restauron.DTO.customer.blockInfo.CustomerBlockInfoDto;
import dev.siraj.restauron.DTO.customer.blockInfo.UnblockRequestDto;
import dev.siraj.restauron.service.customer.blockService.CustomerBlockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/block")
@Slf4j
public class CustomerBlockController {
    private final CustomerBlockService customerBlockService;

    public CustomerBlockController(CustomerBlockService customerBlockService) {
        this.customerBlockService = customerBlockService;
    }

    @GetMapping("/info/{userId}")
    public ResponseEntity<CustomerBlockInfoDto> getBlockInfo(
            @RequestHeader("X-Restaurant-Id") String restaurantEncryptedId,
            @PathVariable("userId") String userId) {

        CustomerBlockInfoDto blockInfo = customerBlockService.getBlockInfo(userId, restaurantEncryptedId);
        return ResponseEntity.ok(blockInfo);
    }

    @PostMapping("/unblock-request/{userId}")
    public ResponseEntity<String> submitUnblockRequest(
            @RequestHeader("X-Restaurant-Id") String restaurantEncryptedId,
            @RequestBody UnblockRequestDto requestDto,
            @PathVariable("userId") String userId) {


        customerBlockService.submitUnblockRequest(userId, restaurantEncryptedId, requestDto);
        return ResponseEntity.ok("Unblock request submitted successfully.");
    }

    @GetMapping("/has-pending-request/{userId}")
    public ResponseEntity<Boolean> hasPendingRequest(
            @RequestHeader("X-Restaurant-Id") String restaurantEncryptedId,
            @PathVariable("userId") String userId) {


        boolean hasPending = customerBlockService.hasActivePendingRequest(userId, restaurantEncryptedId);
        return ResponseEntity.ok(hasPending);
    }
}