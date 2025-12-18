package dev.siraj.restauron.restController.employee.profile;


import dev.siraj.restauron.DTO.employee.PasswordChangeRequestDto;
import dev.siraj.restauron.DTO.owner.EmployeeViewDto;
import dev.siraj.restauron.DTO.owner.UpdateEmployeeRequestDto;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.service.employee.employeeService.EmployeeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee/profile")
@RolesAllowed(roles = {"EMPLOYEE"})
@Slf4j
public class EmployeeProfileController {

    @Autowired private EmployeeService employeeService;

    @GetMapping("/details/{encryptedId}")
    public ResponseEntity<?> getEmployeeDetails(@PathVariable String encryptedId) {

        log.info("Request received to fetch details of employee using ID: {}", encryptedId);

        try {
            EmployeeViewDto employeeViewDto = employeeService.getEmployeeDetailsById(encryptedId);

            return ResponseEntity.ok(employeeViewDto);
        }catch (EntityNotFoundException e) {
            log.warn("Could not find employee with encrypted ID {}: {}", encryptedId, e.getMessage());

            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }catch (Exception e) {
            log.error("An unexpected error occurred while fetching employee details.",e);
            return new ResponseEntity<>("An error occurred on the server.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/update/{encryptedId}")
    public ResponseEntity<?> updateEmployeeDetails(
            @PathVariable String encryptedId,
            @RequestBody UpdateEmployeeRequestDto updateDto
    ){

        log.info("Request received to update details for employee with encrypted ID {}",encryptedId);
        log.info("Change name to : {}, Change personal email to : {}, Change phone to : {}", updateDto.getName(), updateDto.getPersonalEmail(), updateDto.getPhone());

        try {
            EmployeeViewDto employeeViewDto = employeeService.updateEmployeeDetails(encryptedId, updateDto);

            return ResponseEntity.ok(employeeViewDto);
        } catch (EntityNotFoundException e) {
            log.warn("Update failed. Could not find employee with encrypted ID {}: {}", encryptedId, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("An unexpected error occurred while updating employee.", e);
            return new ResponseEntity<>("Failed to update employee.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody PasswordChangeRequestDto passwordDto,
            Authentication authentication
            ){

        String userEmail = authentication.getName();
        log.info("Request received to update password for user: {}", userEmail);

        try{
            employeeService.updateUserPassword(userEmail, passwordDto);
            return ResponseEntity.ok("Password updated successfully.");
        } catch (IllegalArgumentException e) {
            log.warn("Password update failed for user {}: {}", userEmail, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("An unexpected error occurred during password update for user {}", userEmail, e);
            return ResponseEntity.internalServerError().body("An error occurred on the server.");
        }

    }

}
