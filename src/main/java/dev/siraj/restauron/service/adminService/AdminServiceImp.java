package dev.siraj.restauron.service.adminService;

import dev.siraj.restauron.DTO.admin.UserListResponse;
import dev.siraj.restauron.entity.enums.PendingStatuses;
import dev.siraj.restauron.entity.enums.Roles;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.restaurant.RestaurantRegistration;
import dev.siraj.restauron.entity.users.Customer;
import dev.siraj.restauron.entity.users.Employee;
import dev.siraj.restauron.entity.users.Owner;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.mapping.restaurantRegistrationMapping.RestaurantRegistrationMapping;
import dev.siraj.restauron.respository.customerRepo.CustomerRepository;
import dev.siraj.restauron.respository.employeeRepo.EmployeeRepository;
import dev.siraj.restauron.respository.ownerRepo.OwnerRepository;
import dev.siraj.restauron.respository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.respository.userRepo.UserRepository;
import dev.siraj.restauron.service.adminService.adminServiceInterface.AdminService;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import dev.siraj.restauron.service.registrarion.ownerRegistrationService.OwnerRegistrationService;
import dev.siraj.restauron.service.registrarion.registrationInitialService.registrationInitialInterface.RestaurantInitialService;
import dev.siraj.restauron.service.registrarion.restaurantRegistrationService.RestaurantRegistrationService;
import dev.siraj.restauron.service.userService.UserServiceInterface.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImp implements AdminService {

    @Autowired
    private RestaurantInitialService restaurantInitialService;
    @Autowired
    private RestaurantRegistrationMapping restaurantRegistrationMapping;
    @Autowired
    private RestaurantRegistrationService restaurantRegistrationService;
    @Autowired
    private UserService userService;
    @Autowired
    private OwnerRegistrationService ownerRegistrationService;
    @Autowired
    private IdEncryptionService idEncryptionService;
    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private CustomerRepository customerRepository;


    @Override
    @Transactional
    public boolean updateRestaurantRegistrationStatusAndSaveRestaurantAndOwner(Long restaurantId, String statusUpdateTo) {

        RestaurantRegistration restaurantRegistration = restaurantInitialService.findRestaurantRegistrationById(restaurantId);

        // WHEN THE STATUS IS BEING UPDATED INTO APPROVED

        if(statusUpdateTo.equals(PendingStatuses.APPROVED.name())){

            try {

                // register the userAll for authentication purpose - owner
                UserAll user = restaurantRegistrationMapping.restaurantRegistrationToUserForOwner(restaurantRegistration);

                // specified user information based on role for additional information - owner
                Owner owner = ownerRegistrationService.mapToOwnerAndSave(userService.saveUser(user), restaurantRegistration);

                // registering the restaurant from restaurantInitial
                Restaurant restaurant = restaurantRegistrationService.registerRestaurantByRestaurantRegistrationDetailsAndOwner(restaurantRegistration, owner);

                // deleting the restaurant initial after registering the restaurant and owner
                restaurantInitialService.deleteRestaurantRegistrationOnApproval(restaurantRegistration);
            } catch (Exception e) {
                return false;
            }

            return true;

        }

        // WHEN THE STATUS IS BEING UPDATED INTO REJECTED

        else if(statusUpdateTo.equals(PendingStatuses.REJECTED.name())){

            try {
                // method to set the restaurant registration status as rejected
                restaurantInitialService.rejectedByAdmin(restaurantRegistration);
            }catch (Exception e){
                return false;
            }

            return true;
        }


        return false;
    }


    @Override
    public UserListResponse getUserDetailsById(String encryptedId) {
        Long userId = idEncryptionService.decryptToLongId(encryptedId);
        UserAll user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        UserListResponse dto = new UserListResponse();
        dto.setEncryptedId(encryptedId);
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole().name());
        dto.setStatus(user.getStatus().name());


         if (user.getRole() == Roles.OWNER) {
             Owner owner = ownerRepository.findByUser(user);

             if (owner != null) {
                 Restaurant restaurant = restaurantRepository.findByOwner(owner);
                 dto.setRestaurantName(restaurant.getName());
             }
         }

         if(user.getRole() == Roles.EMPLOYEE) {
             Employee employee = employeeRepository.findByUser(user);

             if(employee != null){
                 Restaurant restaurant = employee.getRestaurant();
                 dto.setRestaurantName(restaurant.getName());
             }
         }

         if(user.getRole() == Roles.CUSTOMER){
             Customer customer = customerRepository.findByUser(user);

             if(customer != null){
                 Restaurant restaurant = customer.getRestaurant();

                 dto.setRestaurantName(restaurant.getName());
             }
         }

        return dto;
    }
}
