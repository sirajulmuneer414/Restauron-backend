package dev.siraj.restauron.service.ownerService.interfaces;

import dev.siraj.restauron.DTO.owner.restaurantManagement.TableResponseDto;
import dev.siraj.restauron.entity.restaurant.management.RestaurantTable;
import jakarta.transaction.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface OwnerTableService {
    void createTable(String tableName, Long ownerUserId);


    List<TableResponseDto> getTablesForOwner(Long ownerUserId);


    void deleteTable(String encryptedTableId, Long ownerUserId) throws AccessDeniedException;
}
