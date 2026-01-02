package dev.siraj.restauron.service.registrarion.ownerRegistrationService;

import dev.siraj.restauron.entity.restaurant.RestaurantRegistration;
import dev.siraj.restauron.entity.users.Owner;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.mapping.owner.OwnerUserMapping;
import dev.siraj.restauron.repository.ownerRepo.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OwnerRegistrationServiceImp implements OwnerRegistrationService{

    @Autowired
    private OwnerUserMapping ownerUserMapping;

    @Autowired
    private OwnerRepository ownerRepository;

    @Override
    public Owner mapToOwnerAndSave(UserAll userAll, RestaurantRegistration restaurantRegistration) {

        Owner owner = ownerUserMapping.mapToOwnerFromUserAllAndRestaurantRegistration(userAll, restaurantRegistration);


        return ownerRepository.save(owner);
    }

    @Override
    public Owner findOwnerByUserAll(UserAll user) {
        return ownerRepository.findByUser(user);
    }
}
