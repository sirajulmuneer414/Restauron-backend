package dev.siraj.restauron.DTO.restaurant.config;


import lombok.Data;

// Data Transfer Object for Restaurant Configuration

@Data
public class RestaurantConfigDTO {

        private String restaurantName;

        private String bannerUrl;

        private String primaryColor;

        private String secondaryColor;
        private String buttonTextColor;

        private String centerQuote;
        private String topLeftQuote;
        private String bestFeature;

        private String locationText;
        private String openingTime;
        private String closingTime;

        private boolean isOpenManual;
        private boolean useManualOpen;
}
