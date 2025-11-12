package dev.siraj.restauron.service.customer.profileServices;


import dev.siraj.restauron.DTO.customer.profileGeneral.CustomerStatusDto;
import dev.siraj.restauron.DTO.customer.profileGeneral.CustomerUpdateRequest;
import dev.siraj.restauron.DTO.owner.customerManagement.CustomerResponseDto;
import dev.siraj.restauron.entity.users.Customer;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.respository.customerRepo.CustomerRepository;
import dev.siraj.restauron.respository.userRepo.UserRepository;
import dev.siraj.restauron.service.cloudinaryService.ImageUploadService;
import dev.siraj.restauron.service.customer.blockService.CustomerBlockService;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerProfileServiceImp implements CustomerProfileService {

    @Autowired
    private IdEncryptionService idEncryptionService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageUploadService imageUploadService;

    @Override
    public CustomerStatusDto getCustomerStatusForRestaurant(String userId, String restaurantEncryptedId) {

        Long restaurantId = idEncryptionService.decryptToLongId(restaurantEncryptedId);
        Long userLongId = idEncryptionService.decryptToLongId(userId);

        Customer customer = customerRepository.findByUserIdAndRestaurantId(userLongId, restaurantId).orElseThrow(() -> new EntityNotFoundException("CUSTOMER NOT FOUND - 404"));


        return new CustomerStatusDto(customer.getUser().getStatus());
    }

    @Override
    public CustomerResponseDto getProfile(String encryptedCustomerId) {

        Long customerId = idEncryptionService.decryptToLongId(encryptedCustomerId);

        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        return mapToCustomerResponseDTO(customer);
    }

    @Override
    @Transactional
    public void updateProfile(String encryptedCustomerId, CustomerUpdateRequest updateRequest) {
        Long customerId = idEncryptionService.decryptToLongId(encryptedCustomerId);
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new EntityNotFoundException(""));
        UserAll user = customer.getUser();

        user.setName(updateRequest.getName());
        user.setPhone(updateRequest.getPhone());



        if (updateRequest.getProfilePicture() != null) {
            String imageUrl = imageUploadService.imageUploader(updateRequest.getProfilePicture(), "ProfilePicture-customers-"+customer.getRestaurant().getId());
            customer.setProfilePictureUrl(imageUrl);
        }


        userRepository.save(user);

        customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void deleteAccount(String encryptedCustomerId) {
        Long customerId = idEncryptionService.decryptToLongId(encryptedCustomerId);
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        // This assumes a cascade delete is set up on the User entity

        UserAll user = customer.getUser();

        customerRepository.delete(customer);
        userRepository.delete(user);
    }


    private CustomerResponseDto mapToCustomerResponseDTO(Customer customer) {
        CustomerResponseDto dto = new CustomerResponseDto();
        dto.setName(customer.getUser().getName());
        dto.setEmail(customer.getUser().getEmail());
        dto.setPhone(customer.getUser().getPhone());
        dto.setProfilePictureUrl(customer.getProfilePictureUrl());
        // map other fields as needed
        return dto;
    }
}
