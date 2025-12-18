package dev.siraj.restauron.restController.employee;

import dev.siraj.restauron.DTO.owner.RestaurantReduxSettingDto;
import dev.siraj.restauron.service.employee.employeeService.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeGeneralController {

    @Autowired private EmployeeService employeeService;

    @GetMapping("/get-restaurant-details/{employeeUserId}")
    public ResponseEntity<?> getRestaurantDetailsForEmployeeLogin(@PathVariable String employeeUserId){
        log.info("Inside the get restaurant details after login for employees {}",employeeUserId);

        try {
            RestaurantReduxSettingDto dto = employeeService.findRestaurantByEmployeeFromEncryptedID(employeeUserId);

            log.info("The restaurant {} found from the employee user Encrypted ID : {}",dto.getRestaurantName(),employeeUserId);

            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
        catch (Exception e){
            log.warn("Restaurant no found from the employeeUserId");
        }



        return new ResponseEntity<>("Restaurant not found", HttpStatus.NOT_FOUND);

    }
}
