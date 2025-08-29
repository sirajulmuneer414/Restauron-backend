package dev.siraj.restauron.service.ownerService;

import dev.siraj.restauron.entity.users.Owner;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.respository.ownerRepo.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OwnerServiceImp implements OwnerService{

    @Autowired
    private OwnerRepository ownerRepository;


    @Override
    public Owner findOwnerById(Long ownerId) {

        return ownerRepository.findById(ownerId).get();

    }

    @Override
    public Owner findOwnerByUser(UserAll user) {
        return ownerRepository.findByUser(user);
    }
}
