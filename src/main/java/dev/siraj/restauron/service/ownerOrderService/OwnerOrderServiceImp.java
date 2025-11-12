package dev.siraj.restauron.service.ownerOrderService;


import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.DTO.orders.OrderPageRequestDto;
import dev.siraj.restauron.DTO.owner.orderManagement.OrderDetailDto;
import dev.siraj.restauron.DTO.owner.orderManagement.OrderSummaryDto;
import dev.siraj.restauron.DTO.owner.orderManagement.OwnerOrderRequest;
import dev.siraj.restauron.entity.enums.AccountStatus;
import dev.siraj.restauron.entity.enums.OrderStatus;
import dev.siraj.restauron.entity.enums.OrderType;
import dev.siraj.restauron.entity.orderManagement.Order;
import dev.siraj.restauron.entity.orderManagement.OrderItem;
import dev.siraj.restauron.entity.users.Customer;
import dev.siraj.restauron.entity.users.Employee;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.respository.orderRepo.OrderRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
public class OwnerOrderServiceImp implements OwnerOrderService{


}
