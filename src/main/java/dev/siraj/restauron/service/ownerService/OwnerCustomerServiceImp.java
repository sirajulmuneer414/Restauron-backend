package dev.siraj.restauron.service.ownerService;

import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.DTO.customer.blockInfo.UnblockRequestDetailsDto;
import dev.siraj.restauron.DTO.owner.customerManagement.CustomerDetailDto;
import dev.siraj.restauron.DTO.owner.customerManagement.CustomerResponseDto;
import dev.siraj.restauron.DTO.owner.customerManagement.UpdateCustomerDto;
import dev.siraj.restauron.DTO.owner.customerManagement.UpdateStatusDto;
import dev.siraj.restauron.DTO.owner.orderManagement.CustomerSearchResultDto;
import dev.siraj.restauron.entity.blockAndUnblock.CustomerBlock;
import dev.siraj.restauron.entity.blockAndUnblock.CustomerUnblockRequest;
import dev.siraj.restauron.entity.enums.AccountStatus;
import dev.siraj.restauron.entity.enums.RequestStatus;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.users.Customer;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.respository.blockAndUnblockRepo.CustomerBlockRepository;
import dev.siraj.restauron.respository.blockAndUnblockRepo.CustomerUnblockRequestRepository;
import dev.siraj.restauron.respository.customerRepo.CustomerRepository;
import dev.siraj.restauron.respository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.respository.userRepo.UserRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import dev.siraj.restauron.service.ownerService.interfaces.OwnerCustomerService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class OwnerCustomerServiceImp implements OwnerCustomerService {

    @Autowired private CustomerRepository customerRepository;
    @Autowired private IdEncryptionService idEncryptionService;
    @Autowired private CustomerBlockRepository customerBlockRepository;
    @Autowired private CustomerUnblockRequestRepository customerUnblockRequestRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private RestaurantRepository restaurantRepository;

    @Override
    public Page<CustomerResponseDto> findCustomersByRestaurant(String restaurantEncryptedId, PageRequestDto filter) {
        log.info("Fetching customer list for restaurant. Filter: '{}', Search: '{}'", filter.getFilter(), filter.getSearch());

        Pageable pageable = PageRequest.of(filter.getPageNo(), filter.getSize());

        // Build the dynamic query using Specifications
        Specification<Customer> specification = buildSpecification(restaurantEncryptedId, filter);

        Page<Customer> customerPage = customerRepository.findAll(specification, pageable);

        log.info("Found {} customers on page {}", customerPage.getNumberOfElements(), filter.getPageNo());

        // Map the entity Page to a DTO Page
        return customerPage.map(this::mapToDto);
    }

    @Override
    public CustomerDetailDto findCustomerDetails(String encryptedCustomerId) {
        Long customerId = idEncryptionService.decryptToLongId(encryptedCustomerId);
        Customer customer = findCustomerById(customerId);
        return mapToDetailDto(customer);
    }

    @Override
    @Transactional
    public void updateCustomerStatus(String encryptedCustomerId, UpdateStatusDto statusDto) {
        Long customerId = idEncryptionService.decryptToLongId(encryptedCustomerId);
        Customer customer = findCustomerById(customerId);

        log.info("Updating status for customer '{}' to {}", customer.getUser().getName(), statusDto.getStatus());
        customer.getUser().setStatus(statusDto.getStatus());

        if(statusDto.getStatus().equals(AccountStatus.NONACTIVE)){

            if (statusDto.getSubject() == null || statusDto.getSubject().trim().isEmpty()) {
                throw new IllegalArgumentException("Subject is required when blocking a customer");
            }
            if (statusDto.getDescription() == null || statusDto.getDescription().trim().isEmpty()) {
                throw new IllegalArgumentException("Description is required when blocking a customer");
            }
            CustomerBlock existingBlock = customerBlockRepository.findByUser(customer.getUser()).orElse(null);
            if (existingBlock == null) {
                CustomerBlock customerBlock = new CustomerBlock();
                customerBlock.setUser(customer.getUser());
                customerBlock.setSubject(statusDto.getSubject().trim());
                customerBlock.setDescription(statusDto.getDescription().trim());

                customerBlockRepository.save(customerBlock);
                log.info("Created block record for customer: {}", customer.getUser().getEmail());
            } else {
                // Update existing block record
                existingBlock.setSubject(statusDto.getSubject().trim());
                existingBlock.setDescription(statusDto.getDescription().trim());
                customerBlockRepository.save(existingBlock);
                log.info("Updated existing block record for customer: {}", customer.getUser().getEmail());
            }
        } else if (statusDto.getStatus().equals(AccountStatus.ACTIVE)) {
            // When unblocking, remove the block record
            CustomerBlock customerBlock = customerBlockRepository.findByUser(customer.getUser()).orElse(null);
            CustomerUnblockRequest customerUnblockRequest = customerUnblockRequestRepository.findByCustomer(customer).orElse(null);
            if (customerBlock != null) {
                customerBlockRepository.delete(customerBlock);
                log.info("Removed block record for customer: {}", customer.getUser().getEmail());
            }

            if(customerUnblockRequest != null) {
                customerUnblockRequestRepository.delete(customerUnblockRequest);
                log.info("Removed unblock request record for customer: {}",customer.getId());
            }
        }

        customerRepository.save(customer);
        log.info("Successfully updated customer status to: {}", statusDto.getStatus());
    }


    @Override
    @Transactional
    public void updateCustomerDetails(String encryptedCustomerId, UpdateCustomerDto updateDto) {
        Long customerId = idEncryptionService.decryptToLongId(encryptedCustomerId);
        Customer customer = findCustomerById(customerId);

        UserAll user = customer.getUser();
        log.info("Updating details for customer '{}'", user.getName());
        user.setName(updateDto.getName());
        user.setPhone(updateDto.getPhone());

        customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void deleteCustomer(String encryptedCustomerId) {
        Long customerId = idEncryptionService.decryptToLongId(encryptedCustomerId);
        if (!customerRepository.existsById(customerId)) {
            throw new EntityNotFoundException("Customer not found with ID: " + customerId);
        }
        log.warn("Deleting customer with ID: {}", customerId);
        customerRepository.deleteById(customerId); // This will also delete the UserAll due to CascadeType.REMOVE
    }

    @Override
    @Transactional
    public void approveUnblockRequest(String requestEncryptedId) {

        log.info("Approval for blocked request for requestEncryptedId : {}",requestEncryptedId);

        Long requestId = idEncryptionService.decryptToLongId(requestEncryptedId);

        CustomerUnblockRequest customerUnblockRequest = customerUnblockRequestRepository.findById(requestId).orElseThrow(() -> new EntityNotFoundException("Customer Unblock Request not found"));

        Customer customer = customerUnblockRequest.getCustomer();

        UserAll user = customer.getUser();

        user.setStatus(AccountStatus.ACTIVE);

        userRepository.save(user);

        customerUnblockRequestRepository.delete(customerUnblockRequest);

        CustomerBlock customerBlock = customerBlockRepository.findByUser(user).orElse(null);

        if(customerBlock != null){
            customerBlockRepository.delete(customerBlock);
        }

    }

    @Override
    public void rejectUnblockRequest(String requestEncryptedId, String ownerResponse) {

        log.info("Rejection for blocked request for requestEncryptedId : {}",requestEncryptedId);

        Long requestId = idEncryptionService.decryptToLongId(requestEncryptedId);

        CustomerUnblockRequest customerUnblockRequest = customerUnblockRequestRepository.findById(requestId).orElseThrow(() -> new EntityNotFoundException("Customer Unblock Request not found"));

        Customer customer = customerUnblockRequest.getCustomer();

        UserAll user = customer.getUser();

        user.setStatus(AccountStatus.NONACTIVE);

        userRepository.save(user);

        customerUnblockRequest.setOwnerResponse(ownerResponse);

        customerUnblockRequest.setStatus(RequestStatus.REJECTED);

        customerUnblockRequest.setResolvedAt(LocalDateTime.now());

        customerUnblockRequestRepository.save(customerUnblockRequest);

    }

    @Override
    public CustomerSearchResultDto findCustomerForOwner(String encryptedRestaurantId, String phone, String email) {

        Long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId);

        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new EntityNotFoundException("Restaurant for given ID "+restaurantId+" NOT FOUND"));

        if (StringUtils.hasText(phone)) {
            Customer customer = customerRepository.findByUser_PhoneAndRestaurant(phone, restaurant).orElse(null);

            if(customer != null){
                return convertToSearchResultDto(customer);
            }

        }
        if (StringUtils.hasText(email)) {
            Customer customer = customerRepository.findByUser_EmailAndRestaurant(email, restaurant).orElse(null);

            if(customer != null){
                return convertToSearchResultDto(customer);
            }
        }
        return null;
    }

    private Customer findCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + customerId));
    }

    //          HELPER METHODS              //


    private CustomerSearchResultDto convertToSearchResultDto(Customer customer) {
        return new CustomerSearchResultDto(
                idEncryptionService.encryptLongId(customer.getId()),
                customer.getUser().getName(),
                customer.getUser().getPhone(),
                customer.getUser().getEmail() // Assuming Customer entity has getEmail()
        );
    }

    private CustomerDetailDto mapToDetailDto(Customer customer) {
        CustomerDetailDto dto = new CustomerDetailDto();
        dto.setEncryptedId(idEncryptionService.encryptLongId(customer.getId()));
        dto.setName(customer.getUser().getName());
        dto.setEmail(customer.getUser().getEmail());
        dto.setPhone(customer.getUser().getPhone());
        dto.setStatus(customer.getUser().getStatus());

        if (customer.getUser().getStatus() == AccountStatus.NONACTIVE) {
            CustomerBlock customerBlock = customerBlockRepository.findByUser(customer.getUser()).orElse(null);
            if (customerBlock != null) {
                dto.setBlocked(true);
                dto.setBlockSubject(customerBlock.getSubject());
                dto.setBlockDescription(customerBlock.getDescription());

                // --- NEW: Fetch and map pending unblock requests ---
                List<CustomerUnblockRequest> requests = customerUnblockRequestRepository.findByCustomerId(customer.getId()).orElse(new ArrayList<>());
                dto.setUnblockRequests(requests.stream()
                        .map(this::mapToUnblockRequestDto)
                        .collect(Collectors.toList()));
            } else {
                dto.setBlocked(false);
            }
        } else {
            dto.setBlocked(false);
        }

        return dto;
    }

    private Specification<Customer> buildSpecification(String encryptedRestaurantId, PageRequestDto filter) {
        Long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId);

        return (root, query, criteriaBuilder) -> {
            // Start with the mandatory restaurant filter
            Predicate finalPredicate = criteriaBuilder.equal(root.get("restaurant").get("id"), restaurantId);

            // Add status filter if provided
            if (filter.getFilter() != null) {
                Join<Customer, UserAll> userJoin = root.join("user");
                finalPredicate = criteriaBuilder.and(finalPredicate, criteriaBuilder.equal(userJoin.get("status"), AccountStatus.valueOf(filter.getFilter())));
            }

            // Add search filter (for name or email) if provided
            if (StringUtils.hasText(filter.getSearch())) {
                Join<Customer, UserAll> userJoin = root.join("user"); // Reuse join if already created
                String searchPattern = "%" + filter.getSearch().toLowerCase() + "%";

                Predicate searchPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("name")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("email")), searchPattern)
                );
                finalPredicate = criteriaBuilder.and(finalPredicate, searchPredicate);
            }

            return finalPredicate;
        };
    }

    private CustomerResponseDto mapToDto(Customer customer) {
        CustomerResponseDto dto = new CustomerResponseDto();
        dto.setEncryptedId(idEncryptionService.encryptLongId(customer.getId()));
        dto.setName(customer.getUser().getName());
        dto.setEmail(customer.getUser().getEmail());
        dto.setPhone(customer.getUser().getPhone());
        dto.setStatus(customer.getUser().getStatus());

        if (customer.getUser().getStatus() == AccountStatus.NONACTIVE) {
            CustomerBlock customerBlock = customerBlockRepository.findByUser(customer.getUser()).orElse(null);
            if (customerBlock != null) {
                dto.setBlocked(true);
                dto.setBlockSubject(customerBlock.getSubject());
                dto.setBlockDescription(customerBlock.getDescription());

                // --- NEW: Fetch and map pending unblock requests ---
                List<CustomerUnblockRequest> requests = customerUnblockRequestRepository.findByCustomerId(customer.getId()).orElse(new ArrayList<>());
                dto.setUnblockRequests(requests.stream()
                        .map(this::mapToUnblockRequestDto)
                        .collect(Collectors.toList()));
            } else {
                dto.setBlocked(false);
            }
        } else {
            dto.setBlocked(false);
        }

        return dto;
    }

    private UnblockRequestDetailsDto mapToUnblockRequestDto(CustomerUnblockRequest request) {
        UnblockRequestDetailsDto dto = new UnblockRequestDetailsDto();
        dto.setRequestEncryptedId(idEncryptionService.encryptLongId(request.getId()));
        dto.setCustomerName(request.getCustomer().getUser().getName());
        dto.setCustomerEmail(request.getCustomer().getUser().getEmail());
        dto.setMessage(request.getMessage());
        dto.setStatus(request.getStatus());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setResolvedAt(request.getResolvedAt());
        dto.setOwnerResponse(request.getOwnerResponse());
        return dto;
    }

}
