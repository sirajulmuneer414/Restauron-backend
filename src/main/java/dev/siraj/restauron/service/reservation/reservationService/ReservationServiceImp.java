package dev.siraj.restauron.service.reservation.reservationService;

import dev.siraj.restauron.DTO.reservations.ReservationDto;
import dev.siraj.restauron.entity.enums.ReservationStatus;
import dev.siraj.restauron.entity.restaurant.management.reservation.Reservation;
import dev.siraj.restauron.entity.restaurant.management.reservation.ReservationStatusTimestamp;
import dev.siraj.restauron.respository.reservation.reservationRepo.ReservationRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;


// Service class for reservation operations

@Service
@Slf4j
public class ReservationServiceImp implements ReservationService {

    @Autowired private ReservationRepository reservationRepo;

    /**
     * Service method to fetch all reservation as page with filtering and searching
     *
     * @param pageNo No fo the page for making pageable
     * @param size Size of the page for making pageable
     * @param encryptedRestaurantId Restaurant encrypted id to fetch from db ( Used with specification )
     * @param statusEnum Enum for status filtering ( Used with specification )
     * @param search Search string for filtering ( Used with specification ) - Included for customerName, customerEmail, customerPhone,
     *               reservationDate, reservationTime and reservationDoneBy
     * @return Page of Reservation Entity
     */
    @Override
    public Page<Reservation> findAllPagesWithFilterAndSearch(int pageNo, int size,String encryptedRestaurantId, ReservationStatus statusEnum, String search) {

        Pageable pageable = PageRequest.of(pageNo,size);

        Specification<Reservation> specification = buildSpecification(encryptedRestaurantId, search, statusEnum);

        return reservationRepo.findAll(specification, pageable);

    }

    /**
     * Service method to add Reservation
     * @param encryptedRestaurantId Encrypted restaurant ID
     * @param request ReservationDto Object ( customerEncryptedId,customerName,customerEmail,
     *                customerPhone,reservationDate,reservationTime,noOfPeople,currentStatus,remark)
     * @param reservationDoneBy String mentioning who initiated this reservation
     */
    @Override
    @Transactional
    public void addReservation(String encryptedRestaurantId, ReservationDto request, String reservationDoneBy) {

        Reservation reservation = mapRequestDtoToReservation(new Reservation(),request);

        ReservationStatusTimestamp stamp = new ReservationStatusTimestamp(); // Creating time stamp for currentStatus

        stamp.setStatus(request.getCurrentStatus());
        stamp.setDate(LocalDate.now().toString());
        stamp.setTime(LocalTime.now().toString());
        stamp.setDoneBy(reservationDoneBy);

        reservation.getTimestamps().add(stamp);

        reservation.setReservationDoneBy(reservationDoneBy);

        reservation.setRestaurantEncryptedId(encryptedRestaurantId);

        reservationRepo.save(reservation);

    }


    // ------------------------------------------ HELPER METHODS ----------------------------------------------------------------


    /**
     * Helper method to setup specification
     *
     * @param encryptedRestaurantId Encrypted restaurant ID
     * @param search Search filter
     * @param statusEnum Status filter
     * @return Final Predicate
     */
    private Specification<Reservation> buildSpecification(String encryptedRestaurantId, String search, ReservationStatus statusEnum) {

        return (root, query, criteriaBuilder) -> {
            // Root predicate - always filter by the owner's restaurant
            Predicate finalPredicate = criteriaBuilder.equal(root.get("restaurantEncryptedId"), encryptedRestaurantId);

            // Adding status filter
            if (statusEnum != null) {
                try {


                    // Join with userAll to access status
                    finalPredicate = criteriaBuilder.and(finalPredicate, criteriaBuilder.equal(root.get("currentStatus"), statusEnum));

                } catch (IllegalArgumentException e) {
                    log.warn("Invalid status filter provided: {}", statusEnum);

                }
            }

            // adding searching filter if provided
            if (StringUtils.hasText(search)) {


                String searchPattern = "%" + search.toLowerCase() + "%";

                Predicate searchPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("customerName")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("customerEmail")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("customerPhone")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("reservationDate")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("reservationTime")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("reservationDoneBy")), searchPattern)
                );

                finalPredicate = criteriaBuilder.and(finalPredicate, searchPredicate);
            }

            return finalPredicate;
        };
    }

    /**
     * Helper method to map request DTO to Reservation Entity
     *
     * @param reservation Reservation Entity - Either new() or already saved one to update
     * @param dto ReservationDto Object ( customerEncryptedId,customerName,customerEmail,
     *            customerPhone,reservationDate,reservationTime,noOfPeople,currentStatus,remark)
     * @return Updated or Populated Reservation Object
     */
    private Reservation mapRequestDtoToReservation(Reservation reservation, ReservationDto dto){
        if(dto.getCustomerEncryptedId() != null){
            reservation.setCustomerEncryptedId(dto.getCustomerEncryptedId());
        }

        reservation.setCustomerName(dto.getCustomerName());
        reservation.setCustomerEmail(dto.getCustomerEmail());
        reservation.setCustomerPhone(dto.getCustomerPhone());

        reservation.setReservationDate(dto.getReservationDate());
        reservation.setReservationTime(dto.getReservationTime());

        reservation.setRemark(dto.getRemark());

        reservation.setCurrentStatus(ReservationStatus.valueOf(dto.getCurrentStatus()));

        reservation.setNoOfPeople(dto.getNoOfPeople());

        return reservation;


    }
}
