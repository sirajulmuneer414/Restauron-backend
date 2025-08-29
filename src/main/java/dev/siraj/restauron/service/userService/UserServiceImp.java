package dev.siraj.restauron.service.userService;

import dev.siraj.restauron.DTO.admin.UserListResponse;
import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.entity.enums.Roles;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.mapping.admin.UserListMapping;
import dev.siraj.restauron.respository.userRepo.UserRepository;
import dev.siraj.restauron.service.userService.UserServiceInterface.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImp implements UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserListMapping userListMapping;


    @Override
    public UserAll saveUser(UserAll user) {

        return repository.save(user);
    }

    @Override
    public boolean userExistsByEmailId(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public UserAll findUserByEmail(String email) {
        UserAll user = repository.findByEmail(email);
        System.out.println(user.getId());

        return user;
    }

    // Method to fetch all users and users by filter (Not Admin Roles users)

    @Override
    public Page<UserListResponse> findAllUsersExceptAdminWithFilter(PageRequestDto pageRequestDto) {

        log.info("Inside the service method to fetch user list");
        Pageable pageable = PageRequest.of(pageRequestDto.getPageNo(), pageRequestDto.getSize());
        if(!pageRequestDto.isFiltered()){
            log.info("No filter is found so returning users who is not admin with pagination");

            return  userListMapping.userAllPageToUserResponseDtoPage(repository.findByRoleNot(Roles.ADMIN, pageable));


        }

        String filter = pageRequestDto.getFilter();

        log.info("Filter found :{}", filter);

        if(filter != null){
            return switch (filter.toUpperCase()) {
                case "OWNER" -> userListMapping.userAllPageToUserResponseDtoPage(repository.findByRole(Roles.OWNER, pageable));
                case "EMPLOYEE" -> userListMapping.userAllPageToUserResponseDtoPage(repository.findByRole(Roles.EMPLOYEE, pageable));
                case "CUSTOMER" -> userListMapping.userAllPageToUserResponseDtoPage(repository.findByRole(Roles.CUSTOMER, pageable));
                default -> userListMapping.userAllPageToUserResponseDtoPage(repository.findByRoleNot(Roles.ADMIN, pageable));
            };
        }


        return userListMapping.userAllPageToUserResponseDtoPage(repository.findByRoleNot(Roles.ADMIN, pageable));
    }

    @Override
    public UserAll findUserById(Long userId) {
        return repository.findById(userId).get();
    }
}
