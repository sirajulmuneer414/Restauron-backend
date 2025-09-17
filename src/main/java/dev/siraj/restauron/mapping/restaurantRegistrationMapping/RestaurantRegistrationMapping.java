package dev.siraj.restauron.mapping.restaurantRegistrationMapping;

import dev.siraj.restauron.DTO.admin.RestaurantInitialResponse;
import dev.siraj.restauron.DTO.registration.RestaurantRegistrationDto;
import dev.siraj.restauron.entity.enums.AccountStatus;
import dev.siraj.restauron.entity.enums.PendingStatuses;
import dev.siraj.restauron.entity.enums.Roles;
import dev.siraj.restauron.entity.enums.VerificationStatus;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.restaurant.RestaurantRegistration;
import dev.siraj.restauron.entity.users.Owner;
import dev.siraj.restauron.entity.users.UserAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class RestaurantRegistrationMapping {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static Logger log = LoggerFactory.getLogger(RestaurantRegistrationMapping.class);

    // method to map dto restaurant pre registration to entity for restaurant pre registration
    public RestaurantRegistration registrationDtoToTableEntityMapping(RestaurantRegistrationDto restaurantRegistrationDto){


        log.info("Inside dto to pre");

        RestaurantRegistration restaurantRegistration = new RestaurantRegistration();

        restaurantRegistration.setRestaurantName(restaurantRegistrationDto.getRestaurantName());
        restaurantRegistration.setPassword(passwordEncoder.encode(restaurantRegistrationDto.getPassword()));
        restaurantRegistration.setOwnerName(restaurantRegistrationDto.getName());
        restaurantRegistration.setRestaurantEmail(restaurantRegistrationDto.getRestaurantEmail());
        restaurantRegistration.setOwnerEmail(restaurantRegistrationDto.getEmail());
        restaurantRegistration.setRestaurantAddress(restaurantRegistrationDto.getRestaurantAddress());
        restaurantRegistration.setRestaurantPhone(restaurantRegistrationDto.getRestaurantPhone());
        restaurantRegistration.setOwnerAdhaarNo(restaurantRegistrationDto.getAdhaarNumber());
        restaurantRegistration.setOwnerAdhaarPhoto(restaurantRegistrationDto.getAdhaarPhoto());
        restaurantRegistration.setDistrict(restaurantRegistrationDto.getDistrict());
        restaurantRegistration.setOwnerPhone(restaurantRegistrationDto.getPhone());
        restaurantRegistration.setPincode(restaurantRegistrationDto.getPincode());
        restaurantRegistration.setState(restaurantRegistrationDto.getState());
        restaurantRegistration.setStatus(PendingStatuses.PENDING);
        restaurantRegistration.setOtpStatus(VerificationStatus.PENDING);

        restaurantRegistration.setPassword(passwordEncoder.encode(restaurantRegistrationDto.getPassword()));


        return restaurantRegistration;

    }

    // Method to map all pages of restaurant pre-registration to dto inorder to showcase in frontend
    public Page<RestaurantInitialResponse> restaurantRegistrationToInitialResponsePagingMapping(Page<RestaurantRegistration> all) {
        log.info("Inside method to page of pre restaurant to dto for react");

        return all.map(registration -> {
            RestaurantInitialResponse response = new RestaurantInitialResponse();
            response.setRestaurantId(registration.getId());
            response.setRestaurantName(registration.getRestaurantName());
            response.setRestaurantPhone(registration.getRestaurantPhone());
            response.setOwnerName(registration.getOwnerName());
            response.setOwnerEmail(registration.getOwnerEmail());
            response.setOwnerPhone(registration.getOwnerPhone());
            response.setAdhaarNumber(registration.getOwnerAdhaarNo());
            response.setAdhaarPhoto(registration.getOwnerAdhaarPhoto());
            response.setRestaurantAddress(registration.getRestaurantAddress());
            response.setDistrict(registration.getDistrict());
            response.setState(registration.getState());
            response.setPincode(registration.getPincode());
            response.setStatus(registration.getStatus().name());
            return response;
        });
    }

    // Method for restaurant pre to set up user_all for owner
    public UserAll restaurantRegistrationToUserForOwner(RestaurantRegistration restaurantRegistration) {
        log.info("Inside the pre-restaurant to user_all");

        UserAll user = new UserAll();

        user.setName(restaurantRegistration.getOwnerName());
        user.setEmail(restaurantRegistration.getOwnerEmail());
        user.setPassword(restaurantRegistration.getPassword());
        user.setRole(Roles.OWNER);
        user.setStatus(AccountStatus.ACTIVE);
        user.setPhone(restaurantRegistration.getOwnerPhone());

        return user;
    }


    // Method to set pre-restaurant to restaurant registration
    public Restaurant mapRestaurantRegistrationEntityToRestaurantEntity(RestaurantRegistration restaurantRegistration, Owner owner) {

        log.info("Inside the method for mapping pre-restaurant to restaurant with owner");

        Restaurant restaurant = new Restaurant();

        restaurant.setName(restaurantRegistration.getRestaurantName());
        restaurant.setAddress(restaurantRegistration.getRestaurantAddress());
        restaurant.setEmail(restaurantRegistration.getRestaurantEmail());
        if(restaurantRegistration.getRestaurantPhone().isBlank()){
            restaurant.setPhone(restaurantRegistration.getOwnerPhone());
        }else{
            restaurant.setPhone(restaurantRegistration.getRestaurantPhone());
        }
        restaurant.setOwner(owner);
        restaurant.setEmployeesList(new ArrayList<>());
        restaurant.setDistrict(restaurantRegistration.getDistrict());
        restaurant.setState(restaurantRegistration.getState());
        restaurant.setPincode(restaurantRegistration.getPincode());
        restaurant.setStatus(AccountStatus.ACTIVE);


        return restaurant;

    }
}
