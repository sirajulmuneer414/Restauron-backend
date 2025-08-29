package dev.siraj.restauron.mapping.admin;

import dev.siraj.restauron.DTO.admin.RestaurantInitialResponse;
import dev.siraj.restauron.entity.restaurant.RestaurantRegistration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RestaurantListViewResponseDtoMapping {


    public List<RestaurantInitialResponse> restaurantInitialEntityListToListViewResponseDtoListMapping(List<RestaurantRegistration> restaurantRegistrationList){

        List<RestaurantInitialResponse> returnList = new ArrayList<>();
        for(RestaurantRegistration individualRestaurantRegistrationEntity : restaurantRegistrationList){
            returnList.add(restaurantInitialEntityToListViewResponseDtoMapping(individualRestaurantRegistrationEntity));
        }

        return returnList;
    }


    public RestaurantInitialResponse restaurantInitialEntityToListViewResponseDtoMapping(RestaurantRegistration restaurantRegistration){

        RestaurantInitialResponse restaurantInitialResponse = new RestaurantInitialResponse();


        restaurantInitialResponse.setRestaurantId(restaurantRegistration.getId());
        restaurantInitialResponse.setRestaurantName(restaurantRegistration.getRestaurantName());
        restaurantInitialResponse.setRestaurantPhone(restaurantRegistration.getRestaurantPhone());
        restaurantInitialResponse.setOwnerEmail(restaurantRegistration.getOwnerEmail());
        restaurantInitialResponse.setOwnerName(restaurantRegistration.getOwnerName());
        restaurantInitialResponse.setOwnerPhone(restaurantRegistration.getOwnerPhone());
        restaurantInitialResponse.setStatus(restaurantRegistration.getStatus().name());
        restaurantInitialResponse.setOtpStatus(restaurantRegistration.getOtpStatus().name());


        return restaurantInitialResponse;
    }
}
