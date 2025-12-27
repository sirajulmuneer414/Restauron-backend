package dev.siraj.restauron.service.owner.ownerService;


import dev.siraj.restauron.DTO.owner.restaurantManagement.TableResponseDto;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.restaurant.management.RestaurantTable;
import dev.siraj.restauron.respository.restaurantManagementRepos.RestaurantTableRepository;
import dev.siraj.restauron.respository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import dev.siraj.restauron.service.owner.ownerService.interfaces.OwnerTableService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OwnerTableServiceImp implements OwnerTableService {

    @Autowired private RestaurantTableRepository tableRepository;
    @Autowired private RestaurantRepository restaurantRepository;
    @Autowired private IdEncryptionService idEncryptionService;


    // to create restaurant table
    @Transactional
    @Override
    public void createTable(String tableName, Long ownerUserId) {
        Restaurant restaurant = restaurantRepository.findByOwner_User_Id(ownerUserId).orElseThrow(() -> new RuntimeException("Restaurant not found for owner"));

        RestaurantTable newTable = new RestaurantTable();
        newTable.setName(tableName);
        newTable.setRestaurant(restaurant);
        tableRepository.save(newTable);
    }

    @Override
    public List<TableResponseDto> getTablesForOwner(Long ownerUserId) {
        Restaurant restaurant = restaurantRepository.findByOwner_User_Id(ownerUserId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found for owner"));

        List<RestaurantTable> list = tableRepository.findByRestaurantId(restaurant.getId());
        List<TableResponseDto> resList = new ArrayList<>();

        for(RestaurantTable item : list){

            TableResponseDto dto = new TableResponseDto();
            dto.setEncryptedId(idEncryptionService.encryptLongId(item.getId()));
            dto.setTableId(item.getId());
            dto.setName(item.getName());

            log.info("{}. {}", dto.getEncryptedId(), dto.getName());

            resList.add(dto);
        }


        return resList;
    }

    @Transactional
    @Override
    public void deleteTable(String encryptedTableId, Long ownerUserId) throws AccessDeniedException {
         Long tableId = idEncryptionService.decryptToLongId(encryptedTableId);
         log.info("table Id : {}", tableId );
        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Table not found"));

        // Security check: Ensure the table belongs to the owner's restaurant
        if (!table.getRestaurant().getOwner().getUser().getId().equals(ownerUserId)) {
            throw new AccessDeniedException("You are not authorized to delete this table.");
        }

        tableRepository.deleteById(tableId);
    }
}
