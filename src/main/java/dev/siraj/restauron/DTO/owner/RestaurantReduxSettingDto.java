package dev.siraj.restauron.DTO.owner;

public class RestaurantReduxSettingDto {

    private String restaurantName;

    private String restaurantEncryptedId;

    public String getRestaurantEncryptedId() {
        return restaurantEncryptedId;
    }

    public void setRestaurantEncryptedId(String restaurantEncryptedId) {
        this.restaurantEncryptedId = restaurantEncryptedId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
}
