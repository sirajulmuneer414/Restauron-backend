package dev.siraj.restauron.repository.blockAndUnblockRepo;


import dev.siraj.restauron.entity.blockAndUnblock.CustomerUnblockRequest;
import dev.siraj.restauron.entity.enums.RequestStatus;
import dev.siraj.restauron.entity.users.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface CustomerUnblockRequestRepository extends JpaRepository <CustomerUnblockRequest, Long> {
    Optional <CustomerUnblockRequest> findByCustomerAndStatus(Customer  customer, RequestStatus  status);
    boolean existsByCustomerAndStatus(Customer customer, RequestStatus status);

    Optional<CustomerUnblockRequest> findByCustomer(Customer customer);

    Optional<ArrayList<CustomerUnblockRequest>> findByCustomerId(Long customerId);
}
