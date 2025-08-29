package dev.siraj.restauron.mapping.adminRegistrationMapping;

import dev.siraj.restauron.DTO.registration.AdminRegistrationDto;
import dev.siraj.restauron.entity.enums.AccountStatus;
import dev.siraj.restauron.entity.enums.Roles;
import dev.siraj.restauron.entity.users.Admin;
import dev.siraj.restauron.entity.users.UserAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminRegistrationMapping {

    @Autowired
    private PasswordEncoder passwordEncoder;


    public Admin adminRegistrationDtoToAdminMapping(AdminRegistrationDto dto, UserAll user){

        Admin admin = new Admin();

        admin.setUser(user);
        admin.setAdminStatus(AccountStatus.PENDING);

        return admin;
    }

    public UserAll adminRegistrationDtoToUserAllClassMapping(AdminRegistrationDto dto){

        UserAll user = new UserAll();

        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName());
        user.setStatus(AccountStatus.PENDING);
        user.setRole(Roles.ADMIN);
        user.setPhone(dto.getPhoneNumber());


        return user;

    }
}
