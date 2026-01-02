package dev.siraj.restauron.service.registrarion.adminRegistrationService;


import dev.siraj.restauron.entity.users.Admin;
import dev.siraj.restauron.repository.adminRepo.AdminRepository;
import dev.siraj.restauron.service.registrarion.adminRegistrationService.adminRegistrationInterface.AdminRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminRegistrationServiceImp implements AdminRegistrationService {


    @Autowired
    private AdminRepository repository;

    @Override
    public void saveAdmin(Admin admin) {
        repository.save(admin);
    }
}
