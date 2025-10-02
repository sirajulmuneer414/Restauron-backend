package dev.siraj.restauron.restController.general;


import dev.siraj.restauron.DTO.restaurant.MenuDto;
import dev.siraj.restauron.service.customer.customerMenuService.CustomerMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("public/menu")
@Slf4j
public class CustomerMenuController {

    @Autowired private CustomerMenuService customerMenuService;

    @GetMapping()
    public ResponseEntity<MenuDto> getMenuForCustomer(@RequestHeader("X-Restaurant-Id") String restaurantEncryptedId) {
        log.info("inside the controller for getting menu items , {}",restaurantEncryptedId);
        MenuDto menu = customerMenuService.getFullMenu(restaurantEncryptedId);
        log.info("fetched menu list");
        return ResponseEntity.ok(menu);
    }
}
