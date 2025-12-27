package dev.siraj.restauron.restController.admin;

import dev.siraj.restauron.DTO.admin.RestaurantDetailsDto;
import dev.siraj.restauron.DTO.admin.RestaurantListResponseDto;
import dev.siraj.restauron.DTO.admin.RestaurantUpdateDto;
import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.entity.enums.AccountStatus;
import dev.siraj.restauron.service.adminService.adminServiceInterface.AdminRestaurantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/restaurants")
@Slf4j
public class AdminRestaurantController {

    @Autowired private AdminRestaurantService adminRestaurantService;

    @PostMapping("/fetch-list")
    public ResponseEntity<Page<RestaurantListResponseDto>> fetchRestaurantList(@RequestBody PageRequestDto pageRequestDto) {

        log.info("Inside the controller to fetch restaurant list : {}, {} , {} ", pageRequestDto.getFilter(), pageRequestDto.getSize(), pageRequestDto.getPageNo());
        Page<RestaurantListResponseDto> restaurants = adminRestaurantService.findAllRestaurantsWithFilters(pageRequestDto);
        log.info("Successfully fetched list of restaurants");
        if (restaurants.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/details/{encryptedId}")
    public ResponseEntity<RestaurantDetailsDto> getRestaurantDetails(@PathVariable String encryptedId) {
        log.info("Inside controller to fetch restaurant individual details");
        return ResponseEntity.ok(adminRestaurantService.getRestaurantDetails(encryptedId));
    }

    @PutMapping("/update/{encryptedId}")
    public ResponseEntity<Void> updateRestaurant(@PathVariable String encryptedId, @RequestBody RestaurantUpdateDto dto) {
        log.info("Inside controller to update restaurant");
        adminRestaurantService.updateRestaurant(encryptedId, dto);
        log.info("Successfully updated restaurant");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/block/{encryptedId}")
    public ResponseEntity<Void> blockRestaurant(@PathVariable String encryptedId) {
        log.info("Inside controller to block restaurant");
        adminRestaurantService.updateRestaurantStatus(encryptedId, AccountStatus.NONACTIVE);
        log.info("Successfully blocked restaurant");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unblock/{encryptedId}")
    public ResponseEntity<Void> unblockRestaurant(@PathVariable String encryptedId) {
        log.info("Inside controller to unblock restaurant");
        adminRestaurantService.updateRestaurantStatus(encryptedId, AccountStatus.ACTIVE);
        log.info("Successfully unblocked restaurant");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{encryptedId}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable String encryptedId) {
        log.info("Inside the controller to delete the restaurant");
        adminRestaurantService.deleteRestaurant(encryptedId);
        log.info("Successfully deleted restaurant");
        return ResponseEntity.noContent().build();
    }

}
