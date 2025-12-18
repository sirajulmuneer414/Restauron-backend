package dev.siraj.restauron.service.registrarion.adminRegistrationService.adminRegistrationInterface;

import dev.siraj.restauron.DTO.registration.AdminRegistrationDto;
import dev.siraj.restauron.entity.users.Admin;
import dev.siraj.restauron.entity.users.UserAll;

public interface AdminRegistrationService {

    void saveAdmin(Admin admin);
}
