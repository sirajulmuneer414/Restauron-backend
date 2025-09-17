package dev.siraj.restauron.service.adminService.adminServiceInterface;

import dev.siraj.restauron.DTO.admin.UserEditRequestDto;

public interface AdminUserService {

    void updateUser(String encryptedId, UserEditRequestDto dto);

    void blockUser(String encryptedId);

    void unblockUser(String encryptedId);

    void deleteUser(String encryptedId);

}

