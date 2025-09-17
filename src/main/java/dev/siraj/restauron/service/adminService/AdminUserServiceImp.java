package dev.siraj.restauron.service.adminService;

import dev.siraj.restauron.DTO.admin.UserEditRequestDto;
import dev.siraj.restauron.entity.enums.AccountStatus;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.respository.customerRepo.CustomerRepository;
import dev.siraj.restauron.respository.employeeRepo.EmployeeRepository;
import dev.siraj.restauron.respository.ownerRepo.OwnerRepository;
import dev.siraj.restauron.respository.userRepo.UserRepository;
import dev.siraj.restauron.service.adminService.adminServiceInterface.AdminService;
import dev.siraj.restauron.service.adminService.adminServiceInterface.AdminUserService;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminUserServiceImp implements AdminUserService {

    @Autowired private IdEncryptionService idEncryptionService;
    @Autowired private UserRepository userRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private OwnerRepository ownerRepository;
    @Autowired private EmployeeRepository employeeRepository;


    @Transactional
    @Override
    public void updateUser(String encryptedId, UserEditRequestDto dto) {
        Long id = idEncryptionService.decryptToLongId(encryptedId);
        UserAll user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));

        user.setName(dto.getName());
        user.setPhone(dto.getPhone());

        userRepository.save(user);
    }

    @Transactional
    @Override
    public void blockUser(String encryptedId) {
        Long id = idEncryptionService.decryptToLongId(encryptedId);
        UserAll user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));

        user.setStatus(AccountStatus.NONACTIVE);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void unblockUser(String encryptedId) {
        Long id = idEncryptionService.decryptToLongId(encryptedId);
        UserAll user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));

        user.setStatus(AccountStatus.ACTIVE);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteUser(String encryptedId) {
        Long id = idEncryptionService.decryptToLongId(encryptedId);

        UserAll user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found with ID: "+id));

        switch (user.getRole()){

            case CUSTOMER -> customerRepository.deleteByUser(user);

            case OWNER -> ownerRepository.deleteByUser(user);

            case EMPLOYEE -> employeeRepository.deleteByUser(user);

            default -> log.warn("The role is either admin or not in the list");
        }



    }



}
