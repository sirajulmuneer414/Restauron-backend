package dev.siraj.restauron.service.adminService.adminServiceInterface;

import dev.siraj.restauron.DTO.admin.RestaurantDetailsDto;
import dev.siraj.restauron.DTO.admin.RestaurantListResponseDto;
import dev.siraj.restauron.DTO.admin.RestaurantUpdateDto;
import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.entity.enums.AccountStatus;
import org.springframework.data.domain.Page;

public interface AdminRestaurantService {
    Page<RestaurantListResponseDto> findAllRestaurantsWithFilters(PageRequestDto pageRequestDto);

    RestaurantDetailsDto getRestaurantDetails(String encryptedId);

    void updateRestaurant(String encryptedId, RestaurantUpdateDto dto);

    void updateRestaurantStatus(String encryptedId, AccountStatus newStatus);

    void deleteRestaurant(String encryptedId);
}
