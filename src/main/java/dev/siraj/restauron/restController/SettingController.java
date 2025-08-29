package dev.siraj.restauron.restController;

import dev.siraj.restauron.entity.enums.AccountStatus;
import dev.siraj.restauron.entity.enums.Roles;
import dev.siraj.restauron.entity.users.Admin;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.service.registrarion.adminRegistrationService.adminRegistrationInterface.AdminRegistrationService;
import dev.siraj.restauron.service.userService.UserServiceInterface.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Class to set up any program that needs to be run the application is being initiated
@Component
public class SettingController implements CommandLineRunner {


    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;
    @Autowired
    private AdminRegistrationService adminRegistrationService;

    private final String ADMIN_SECURITY_CODE = "KJ9MP4XN7QRTY2VL8WZ3";
    private final String ADMIN_EMAIL_ID = "Admin@restauron.com";
    private final String ADMIN_PHONE_NUMBER = "8943253154";
    private final Logger log = LoggerFactory.getLogger(SettingController.class);


    // Method to check in the database whether admin exists or not and setting up the admin if not

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if(userService.userExistsByEmailId(ADMIN_EMAIL_ID)){
            log.info("Admin already exists, exiting the method");
            return;}


        UserAll user = new UserAll();
        user.setEmail(ADMIN_EMAIL_ID);
        user.setPassword(passwordEncoder.encode(ADMIN_SECURITY_CODE));
        user.setPhone(ADMIN_PHONE_NUMBER);
        user.setRole(Roles.ADMIN);
        user.setStatus(AccountStatus.ACTIVE);
        user.setName("ADMIN");

        user = userService.saveUser(user);

        log.info("UserAll for admin created successfully");

        Admin admin = new Admin();

        admin.setUser(user);
        admin.setAdminStatus(AccountStatus.ACTIVE);

        adminRegistrationService.saveAdmin(admin);

        log.info("Admin created successfully, exiting the method");
    }
}

