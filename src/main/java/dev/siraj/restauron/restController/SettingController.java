package dev.siraj.restauron.restController;

import dev.siraj.restauron.entity.enums.AccountStatus;
import dev.siraj.restauron.entity.enums.Roles;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.users.Admin;
import dev.siraj.restauron.entity.users.Customer;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.respository.customerRepo.CustomerRepository;
import dev.siraj.restauron.respository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.service.registrarion.adminRegistrationService.adminRegistrationInterface.AdminRegistrationService;
import dev.siraj.restauron.service.reservation.reservationAvailability.weeklyAvailabilityService.WeeklyAvailabilityService;
import dev.siraj.restauron.service.userService.UserServiceInterface.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Class to set up any program that needs to be run the application is being initiated
@Component
public class SettingController implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final AdminRegistrationService adminRegistrationService;
    private final WeeklyAvailabilityService weeklyAvailabilityService;
    private final String ADMIN_SECURITY_CODE;

    private static final String ADMIN_EMAIL_ID = "Admin@restauron.com";
    private static final String ADMIN_PHONE_NUMBER = "8943253154";
    private static final Logger log = LoggerFactory.getLogger(SettingController.class);

    // Use constructor injection for all dependencies
    @Autowired
    public SettingController(
            PasswordEncoder passwordEncoder,
            UserService userService,
            AdminRegistrationService adminRegistrationService,
            WeeklyAvailabilityService weeklyAvailabilityService,
            @Value("${app.admin-password}") String adminSecurityCode) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.adminRegistrationService = adminRegistrationService;
        this.ADMIN_SECURITY_CODE = adminSecurityCode;
        this.weeklyAvailabilityService = weeklyAvailabilityService;
    }
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

