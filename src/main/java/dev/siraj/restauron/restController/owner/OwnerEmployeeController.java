package dev.siraj.restauron.restController.owner;

import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.DTO.owner.EmployeeRegistrationRequestDto;
import dev.siraj.restauron.DTO.owner.EmployeeViewDto;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.service.employeeManagementService.EmployeeManagementService;
import dev.siraj.restauron.service.restaurantService.restaurantServiceInterface.RestaurantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/*
        This class is to set rest controller for dealing with all request related to employees
        from the owner side
 */

@RestController
@RequestMapping("/owner/employees")
@RolesAllowed(roles = {"OWNER"})
@Slf4j
public class OwnerEmployeeController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private EmployeeManagementService employeeManagementService;


    @PostMapping("/fetch-list")
    public ResponseEntity<Page<EmployeeViewDto>> fetchEmployeeListUsingFilter(@RequestBody PageRequestDto pageRequestDto, @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId){

        log.info("Received request to fetch employees list of {} restaurant",encryptedRestaurantId);

        try{

            Page<EmployeeViewDto> employeePage = employeeManagementService.fetchEmployees(pageRequestDto, encryptedRestaurantId);

            return new ResponseEntity<>(employeePage, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error fetching employee list: {}", e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    @PostMapping("/add")
    public ResponseEntity<String> addEmployee(@RequestBody EmployeeRegistrationRequestDto dto, @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId){

        log.info("Received request to add employee {}", dto.getName());

        try{
            employeeManagementService.addEmployee(dto, encryptedRestaurantId);

            return new ResponseEntity<>("Employee added successfully.", HttpStatus.CREATED);
        }catch (Exception e){
            log.error("Error adding employee: {}", e.getMessage(), e);

            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
}
