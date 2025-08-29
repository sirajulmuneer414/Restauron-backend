package dev.siraj.restauron.mapping.admin;

import dev.siraj.restauron.DTO.admin.UserListResponse;
import dev.siraj.restauron.entity.users.Owner;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.respository.ownerRepo.OwnerRepository;
import dev.siraj.restauron.respository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import dev.siraj.restauron.service.registrarion.ownerRegistrationService.OwnerRegistrationService;
import dev.siraj.restauron.service.restaurantService.restaurantServiceInterface.RestaurantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
/*
    This class is to map UserAll to Dto (request from Admin Side)
 */
@Component
@Slf4j
public class UserListMapping {

    @Autowired
    private IdEncryptionService idEncryptionService;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;


    public Page<UserListResponse> userAllPageToUserResponseDtoPage(Page<UserAll> all) {
        log.info("Inside method to page of pre restaurant to dto for react");

        return all.map(user -> {
            UserListResponse response = new UserListResponse();
            response.setEncryptedId(idEncryptionService.encryptLongId(user.getId()));
            response.setName(user.getName());
            response.setStatus(user.getStatus().name());
            response.setRole(user.getRole().name());
            response.setEmail(user.getEmail());

            switch (user.getRole().name()){
                case "OWNER" :
                    Owner owner = ownerRepository.findByUser(user);
                    response.setRestaurantName(restaurantRepository.findByOwner(owner).getName());
                    break;
                case "EMPLOYEE" :
                    break;
                case "CUSTOMER" :
                    break;
            }

            return response;
        });
    }

}
