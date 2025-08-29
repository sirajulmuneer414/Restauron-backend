package dev.siraj.restauron.service.ownerService;

import dev.siraj.restauron.entity.users.Owner;
import dev.siraj.restauron.entity.users.UserAll;

public interface OwnerService {
    Owner findOwnerById(Long ownerId);

    Owner findOwnerByUser(UserAll user);
}
