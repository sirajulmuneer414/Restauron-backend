package dev.siraj.restauron.restController.admin;

import dev.siraj.restauron.DTO.admin.UserListResponse;
import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.service.userService.UserServiceInterface.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/*
    RestController Class to deal with every API regarding users from Admin Side

 */

@RestController
@RequestMapping("/admin/users")
@RolesAllowed(roles = {"ADMIN"})
public class AdminUserAllController {

    @Autowired
    private UserService userService;

    private final Logger log = LoggerFactory.getLogger(AdminUserAllController.class);

    // This method is for fetching all user ( Filters and Pagination applicable )
    @PostMapping("/fetch-list")
    public ResponseEntity<?> fetchUserFullListWithFiltersAndPagination(@RequestBody PageRequestDto pageRequestDto){
        log.info("In the userAll list fetch method from admin side");


        Page<UserListResponse> list = userService.findAllUsersExceptAdminWithFilter(pageRequestDto);


        if(list.isEmpty()){
            return ResponseEntity.status(204).body(null);
        }

        return new ResponseEntity<>(list, HttpStatus.OK);





    }
}
