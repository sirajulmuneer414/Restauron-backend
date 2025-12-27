package dev.siraj.restauron.service.adminService;

import dev.siraj.restauron.DTO.admin.RestaurantDetailsDto;
import dev.siraj.restauron.DTO.admin.RestaurantUpdateDto;
import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.entity.enums.AccountStatus;
import dev.siraj.restauron.service.adminService.adminServiceInterface.AdminRestaurantService;

import dev.siraj.restauron.DTO.admin.RestaurantListResponseDto;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.respository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import dev.siraj.restauron.specification.RestaurantSpecification;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdminRestaurantServiceImp implements AdminRestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private IdEncryptionService idEncryptionService;


    @Override
    public Page<RestaurantListResponseDto> findAllRestaurantsWithFilters(PageRequestDto pageRequestDto) {
        log.info("Started creating specification.....");
        Specification<Restaurant> spec = RestaurantSpecification.withDynamicQuery(
                pageRequestDto.getFilter(),
                pageRequestDto.getSearch()
        );
        log.info("Finished Creating Specification!");
        Pageable pageable = PageRequest.of(pageRequestDto.getPageNo(), pageRequestDto.getSize());

        log.info("Finished creating pagination");

        Page<Restaurant> restaurantPage = restaurantRepository.findAll(spec, pageable);

        log.info("Finished fetching from the data base");

        return restaurantPage.map(this::convertToDto);
    }


    @Override
    public RestaurantDetailsDto getRestaurantDetails(String encryptedId) {
        Long restaurantId = idEncryptionService.decryptToLongId(encryptedId);
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found when checking for restaurant details"));

        return convertToDetailsDto(restaurant);
    }

    // --- UPDATE ---
    @Override
    @Transactional
    public void updateRestaurant(String encryptedId, RestaurantUpdateDto dto) {
        Long restaurantId = idEncryptionService.decryptToLongId(encryptedId);
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found when updating restaurant"));

        restaurant.setName(dto.getName());
        restaurant.setEmail(dto.getEmail());
        restaurant.setPhone(dto.getPhone());
        restaurant.setAddress(dto.getAddress());
        restaurant.setDistrict(dto.getDistrict());
        restaurant.setState(dto.getState());
        restaurant.setPincode(dto.getPincode());

        restaurantRepository.save(restaurant);
    }

    // --- BLOCK / UNBLOCK ---
    @Transactional
    @Override
    public void updateRestaurantStatus(String encryptedId, AccountStatus newStatus) {
        Long restaurantId = idEncryptionService.decryptToLongId(encryptedId);
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found when updating status"));

        restaurant.setStatus(newStatus);
        restaurantRepository.save(restaurant);
    }

    // --- DELETE ---
    @Transactional
    @Override
    public void deleteRestaurant(String encryptedId) {
        Long restaurantId = idEncryptionService.decryptToLongId(encryptedId);
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new EntityNotFoundException("Restaurant not found when deleting restaurant");
        }
        // Note: The @OneToMany(cascade=CascadeType.ALL) on employees and
        // @OneToOne(cascade=CascadeType.REMOVE) on owner will handle deletion of dependents.
        restaurantRepository.deleteById(restaurantId);
    }

    // --- DTO CONVERTER --- //
    private RestaurantDetailsDto convertToDetailsDto(Restaurant restaurant) {
        RestaurantDetailsDto dto = new RestaurantDetailsDto();
        dto.setEncryptedId(idEncryptionService.encryptLongId(restaurant.getId()));
        dto.setName(restaurant.getName());
        dto.setEmail(restaurant.getEmail());
        dto.setPhone(restaurant.getPhone());
        dto.setAddress(restaurant.getAddress());
        dto.setDistrict(restaurant.getDistrict());
        dto.setState(restaurant.getState());
        dto.setPincode(restaurant.getPincode());
        dto.setStatus(restaurant.getStatus().name());

        if (restaurant.getOwner() != null && restaurant.getOwner().getUser() != null) {
            dto.setOwnerName(restaurant.getOwner().getUser().getName());
            dto.setOwnerEncryptedUserId(idEncryptionService.encryptLongId(restaurant.getOwner().getUser().getId()));
        }

        return dto;
    }

    private RestaurantListResponseDto convertToDto(Restaurant restaurant) {
        RestaurantListResponseDto dto = new RestaurantListResponseDto();
        dto.setEncryptedId(idEncryptionService.encryptLongId(restaurant.getId()));
        dto.setName(restaurant.getName());
        dto.setEmail(restaurant.getEmail());
        dto.setPhone(restaurant.getPhone());
        dto.setOwnerName(restaurant.getOwner().getUser().getName());
        dto.setStatus(restaurant.getStatus().name()); // Uncomment when you have a status field
        return dto;
    }



}