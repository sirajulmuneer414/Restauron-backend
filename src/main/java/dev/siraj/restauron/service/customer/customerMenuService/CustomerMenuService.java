package dev.siraj.restauron.service.customer.customerMenuService;

import dev.siraj.restauron.DTO.restaurant.MenuDto;

public interface CustomerMenuService {
    MenuDto getFullMenu(String restaurantEncryptedId);
}
