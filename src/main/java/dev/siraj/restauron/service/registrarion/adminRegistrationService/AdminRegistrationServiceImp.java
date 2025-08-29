package dev.siraj.restauron.service.registrarion.adminRegistrationService;


import dev.siraj.restauron.DTO.registration.AdminRegistrationDto;
import dev.siraj.restauron.entity.enums.AccountStatus;
import dev.siraj.restauron.entity.users.Admin;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.mapping.adminRegistrationMapping.AdminRegistrationMapping;
import dev.siraj.restauron.respository.adminRepo.AdminRepository;
import dev.siraj.restauron.service.registrarion.adminRegistrationService.adminRegistrationInterface.AdminRegistrationService;
import dev.siraj.restauron.service.registrarion.otpService.otpInterface.OtpService;
import dev.siraj.restauron.service.userService.UserServiceInterface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminRegistrationServiceImp implements AdminRegistrationService {

    @Autowired
    private AdminRegistrationMapping mapping;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminRepository repository;

    @Autowired
    private OtpService otpService;


    private final String ADMIN_SECURITY_CODE = "KJ9MP4XN7QRTY2VL8WZ3";


    @Override
    public boolean verifyAdminCode(String code) {

        boolean res = ADMIN_SECURITY_CODE.equals(code);

        System.out.println(res);

        return res;
    }

    @Override
    public boolean registerAdminAndSentOtpForVerification(AdminRegistrationDto adminRegistrationDto) {

        UserAll user = mapping.adminRegistrationDtoToUserAllClassMapping(adminRegistrationDto);

        UserAll userToSave = userService.saveUser(user);

        Admin admin = mapping.adminRegistrationDtoToAdminMapping(adminRegistrationDto,userToSave);

        Admin adminSaved = repository.save(admin);


        try {
           String otp = otpService.sendOtpForAdminRegistration(user.getName(), user.getEmail(), adminSaved.getId());
            System.out.println(otp);

        }catch (Exception e) {
            return false;
        }

       return true;

    }

    @Override
    public Admin findAdminByUserAll(UserAll user) {
        Admin admin = repository.findByUser(user);
        if(admin == null) System.out.println("The admin is null");
        else System.out.println(admin.getId());

        return admin;
    }

    @Override
    public void otpVerificationSuccessfulEnumChange(Admin admin) {
        admin.setAdminStatus(AccountStatus.ACTIVE);
        repository.save(admin);
    }

    @Override
    public void saveAdmin(Admin admin) {
        repository.save(admin);
    }
}
