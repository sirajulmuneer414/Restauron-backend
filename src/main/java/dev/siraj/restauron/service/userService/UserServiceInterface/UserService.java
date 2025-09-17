package dev.siraj.restauron.service.userService.UserServiceInterface;

import dev.siraj.restauron.DTO.admin.UserListResponse;
import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.entity.users.UserAll;
import org.springframework.data.domain.Page;


public interface UserService {
    UserAll saveUser(UserAll user);

    boolean userExistsByEmailId(String email);

    UserAll findUserByEmail(String email);

    Page<UserListResponse> findAllUsersExceptAdminWithFilter(PageRequestDto pageRequestDto);

    UserAll findUserById(Long userId);

    void save(UserAll user);
}
