package dev.siraj.restauron.service.owner.ownerService.interfaces;

import dev.siraj.restauron.DTO.owner.restaurantManagement.TableResponseDto;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface OwnerTableService {
    void createTable(String tableName, Integer capacity, Long ownerUserId);


    List<TableResponseDto> getTablesForOwner(Long ownerUserId);


    void deleteTable(String encryptedTableId, Long ownerUserId) throws AccessDeniedException;
}
