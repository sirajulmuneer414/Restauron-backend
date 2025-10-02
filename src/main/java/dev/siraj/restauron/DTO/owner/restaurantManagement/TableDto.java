package dev.siraj.restauron.DTO.owner.restaurantManagement;


import lombok.Data;

    @Data
    public class TableDto {
        private String encryptedId;
        private String name;
        private String qrCodeUrl; // This will be constructed on the frontend
    }

