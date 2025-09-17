package dev.siraj.restauron.restController.admin;

import dev.siraj.restauron.DTO.admin.UserEditRequestDto;
import dev.siraj.restauron.DTO.admin.UserListResponse;
import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.service.adminService.adminServiceInterface.AdminService;
import dev.siraj.restauron.service.adminService.adminServiceInterface.AdminUserService;
import dev.siraj.restauron.service.userService.UserServiceInterface.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/*
    RestController Class to deal with every API regarding users from Admin Side

 */

@RestController
@RequestMapping("/admin/users")
@RolesAllowed(roles = {"ADMIN"})
public class AdminUserAllController {

    @Autowired
    private UserService userService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private AdminUserService adminUserService;

    private final Logger log = LoggerFactory.getLogger(AdminUserAllController.class);

    // This method is for fetching all user ( Filters and Pagination applicable )
    @PostMapping("/fetch-list")
    public ResponseEntity<?> fetchUserFullListWithFiltersAndPagination(@RequestBody PageRequestDto pageRequestDto){
        log.info("In the userAll list fetch method from admin side");


        Page<UserListResponse> list = userService.findAllUsersExceptAdminWithFilter(pageRequestDto);


        if(list.isEmpty()){
            return ResponseEntity.status(204).body(null);
        }

        log.info("UserAll list fetched successfully");
        return new ResponseEntity<>(list, HttpStatus.OK);


    }

    @GetMapping("/details/{encryptedId}")
    public ResponseEntity<UserListResponse> getUserDetails(@PathVariable String encryptedId) {
       log.info("In the controller to fetch individual user details");

        UserListResponse userDetails = adminService.getUserDetailsById(encryptedId);

        log.info("Successfully fetched individual user details");
        return ResponseEntity.ok(userDetails);
    }
    @PutMapping("/update/{encryptedId}")
    public ResponseEntity<Void> updateUser(@PathVariable String encryptedId, @Valid @RequestBody UserEditRequestDto dto) {

        log.info("Inside the controller to update user details");

        adminUserService.updateUser(encryptedId, dto);

        log.info("Successfully updated user details");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/block/{encryptedId}")
    public ResponseEntity<Void> blockUser(@PathVariable String encryptedId) {

        log.info("Inside the controller to block the user");

        adminUserService.blockUser(encryptedId);

        log.info("Successfully blocked user with ID : {}", encryptedId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unblock/{encryptedId}")
    public ResponseEntity<Void> unblockUser(@PathVariable String encryptedId) {

        log.info("Inside the controller to unblock user");

        adminUserService.unblockUser(encryptedId);

        log.info("Successfully unblocked user with ID : {}",encryptedId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{encryptedId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String encryptedId) {

        log.info("Inside the controller to delete user");

        adminUserService.deleteUser(encryptedId);

        log.info("Successfully deleted user with ID : {}",encryptedId);
        return ResponseEntity.noContent().build();
    }


}
