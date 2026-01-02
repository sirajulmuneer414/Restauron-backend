package dev.siraj.restauron.service.customer.blockService;

import dev.siraj.restauron.DTO.customer.blockInfo.CustomerBlockInfoDto;
import dev.siraj.restauron.DTO.customer.blockInfo.UnblockRequestDto;
import dev.siraj.restauron.entity.blockAndUnblock.CustomerBlock;
import dev.siraj.restauron.entity.blockAndUnblock.CustomerUnblockRequest;
import dev.siraj.restauron.entity.enums.RequestStatus;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.users.Customer;
import dev.siraj.restauron.repository.blockAndUnblockRepo.CustomerBlockRepository;
import dev.siraj.restauron.repository.blockAndUnblockRepo.CustomerUnblockRequestRepository;
import dev.siraj.restauron.repository.customerRepo.CustomerRepository;
import dev.siraj.restauron.repository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomerBlockServiceImp implements CustomerBlockService{

    @Autowired
    private CustomerBlockRepository  customerBlockRepository;

    @Autowired
    private CustomerRepository  customerRepository;

    @Autowired
    private RestaurantRepository  restaurantRepository;

    @Autowired
    private CustomerUnblockRequestRepository customerUnblockRequestRepository;

    @Autowired
    private IdEncryptionService idEncryptionService;

    @Override
    public CustomerBlockInfoDto getBlockInfo(String userId, String restaurantEncryptedId) {
        Long restaurantId = idEncryptionService.decryptToLongId(restaurantEncryptedId);
        Long userLongId = idEncryptionService.decryptToLongId(userId);
        Customer customer = customerRepository.findByUserIdAndRestaurantId(userLongId, restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        CustomerBlock customerBlock = customerBlockRepository.findByUser(customer.getUser())
                .orElseThrow(() -> new EntityNotFoundException("Block information not found"));

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        CustomerBlockInfoDto dto = new CustomerBlockInfoDto();
        dto.setSubject(customerBlock.getSubject());
        dto.setDescription(customerBlock.getDescription());
        dto.setBlockedAt(customerBlock.getBlockedAt());
        dto.setRestaurantName(restaurant.getName());
        dto.setHasPendingRequest(customerUnblockRequestRepository.existsByCustomerAndStatus(customer, RequestStatus.PENDING));

        return dto;
    }

    @Override
    @Transactional
    public void submitUnblockRequest(String userEncryptedId, String restaurantEncryptedId, UnblockRequestDto requestDto) {
        Long restaurantId = idEncryptionService.decryptToLongId(restaurantEncryptedId);
        Long userId = idEncryptionService.decryptToLongId(userEncryptedId);

        Customer customer = customerRepository.findByUserIdAndRestaurantId(userId, restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        if (customerUnblockRequestRepository.existsByCustomerAndStatus(customer, RequestStatus.PENDING)) {
            throw new IllegalStateException("You already have a pending unblock request.");
        }

        CustomerUnblockRequest request = new CustomerUnblockRequest();
        request.setCustomer(customer);
        request.setRestaurant(restaurant);
        request.setMessage(requestDto.getMessage());
        request.setStatus(RequestStatus.PENDING);

        customerUnblockRequestRepository.save(request);
        log.info("Created unblock request for customer: {} at restaurant: {}", userEncryptedId, restaurant.getName());
    }

    @Override
    public boolean hasActivePendingRequest(String userEncryptedId, String restaurantEncryptedId) {
        Long restaurantId = idEncryptionService.decryptToLongId(restaurantEncryptedId);
        Long userId = idEncryptionService.decryptToLongId(userEncryptedId);

        Customer customer = customerRepository.findByUserIdAndRestaurantId(userId, restaurantId)
                .orElse(null);

        return customer != null && customerUnblockRequestRepository .existsByCustomerAndStatus(customer, RequestStatus.PENDING);
    }
}