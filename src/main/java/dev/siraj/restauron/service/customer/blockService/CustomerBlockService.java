package dev.siraj.restauron.service.customer.blockService;

import dev.siraj.restauron.DTO.customer.blockInfo.CustomerBlockInfoDto;
import dev.siraj.restauron.DTO.customer.blockInfo.UnblockRequestDto;

public interface CustomerBlockService {
    CustomerBlockInfoDto getBlockInfo(String customerEmail, String restaurantEncryptedId);
    void submitUnblockRequest(String customerEmail, String restaurantEncryptedId, UnblockRequestDto requestDto);
    boolean hasActivePendingRequest(String customerEmail, String restaurantEncryptedId);
}

