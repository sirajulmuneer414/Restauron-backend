package dev.siraj.restauron.service.customer.profileServices;


import dev.siraj.restauron.DTO.customer.profileGeneral.CustomerStatusDto;
import dev.siraj.restauron.entity.users.Customer;
import dev.siraj.restauron.respository.customerRepo.CustomerRepository;
import dev.siraj.restauron.service.customer.blockService.CustomerBlockService;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerProfileServiceImp implements CustomerProfileService {

    @Autowired
    private IdEncryptionService idEncryptionService;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public CustomerStatusDto getCustomerStatusForRestaurant(String userId, String restaurantEncryptedId) {

        Long restaurantId = idEncryptionService.decryptToLongId(restaurantEncryptedId);
        Long userLongId = idEncryptionService.decryptToLongId(userId);

        Customer customer = customerRepository.findByUserIdAndRestaurantId(userLongId, restaurantId).orElseThrow(() -> new EntityNotFoundException("CUSTOMER NOT FOUND - 404"));


        return new CustomerStatusDto(customer.getUser().getStatus());
    }
}
