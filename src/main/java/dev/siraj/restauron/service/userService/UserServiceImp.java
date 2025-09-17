package dev.siraj.restauron.service.userService;

import dev.siraj.restauron.DTO.admin.UserListResponse;
import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.entity.enums.Roles;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.mapping.admin.UserListMapping;
import dev.siraj.restauron.respository.userRepo.UserRepository;
import dev.siraj.restauron.service.userService.UserServiceInterface.UserService;
import dev.siraj.restauron.specification.UserSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
        log.info("Fetching user list with filter: '{}' and search: '{}'", pageRequestDto.getFilter(), pageRequestDto.getSearch());

        // Create a Specification with the dynamic filter and search criteria
        Specification<UserAll> spec = UserSpecification.withDynamicQuery(
                pageRequestDto.getFilter(),
                pageRequestDto.getSearch()
        );

        Pageable pageable = PageRequest.of(pageRequestDto.getPageNo(), pageRequestDto.getSize());

        // Execute the query using the specification
        Page<UserAll> userPage = repository.findAll(spec, pageable);

        // Map to DTO
        return userListMapping.userAllPageToUserResponseDtoPage(userPage);
    }

    @Override
    public UserAll findUserById(Long userId) {
        return repository.findById(userId).get();
    }

    @Override
    public void save(UserAll user) {
        repository.save(user);
    }
}
