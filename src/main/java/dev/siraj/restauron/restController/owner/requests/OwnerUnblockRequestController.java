package dev.siraj.restauron.restController.owner.requests;


import dev.siraj.restauron.DTO.owner.customerManagement.RejectRequestDto;
import dev.siraj.restauron.service.owner.ownerService.interfaces.OwnerCustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/owner/unblock-requests")
@RequiredArgsConstructor
public class OwnerUnblockRequestController {

    private final OwnerCustomerService ownerCustomerService;

    @PostMapping("/approve/{requestEncryptedId}")
    public ResponseEntity<Void> approveRequest(@PathVariable String requestEncryptedId) {
        log.info("in controller for approval");
        ownerCustomerService.approveUnblockRequest(requestEncryptedId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reject/{requestEncryptedId}")
    public ResponseEntity<Void> rejectRequest(
            @PathVariable String requestEncryptedId,
            @RequestBody RejectRequestDto rejectDto) {
        log.info("in controller for rejection");
        ownerCustomerService.rejectUnblockRequest(requestEncryptedId, rejectDto.getOwnerResponse());
        return ResponseEntity.ok().build();
    }
}

