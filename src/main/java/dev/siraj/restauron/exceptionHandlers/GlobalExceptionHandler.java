package dev.siraj.restauron.exceptionHandlers;

import com.razorpay.RazorpayException;
import dev.siraj.restauron.exceptionHandlers.customExceptions.ServiceNotAvailableException;
import dev.siraj.restauron.exceptionHandlers.errorResponseDto.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

    // Global exception handler for REST controllers

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {



    /** Handle AccessDeniedException globally.
     *
     * @param e the AccessDeniedException
     * @return ResponseEntity with error message and HTTP status 500
     */
    @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<String> accessDeniedExceptionHandler(AccessDeniedException e){

            log.info("Access denied exception: {} ", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    /** Handle EntityNotFoundException globally.
     *
     * @param e the EntityNotFoundException
     * @return ResponseEntity with error message and HTTP status 404
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> entityNotFoundExceptionHandler(EntityNotFoundException e){
        log.error("Entity not found exception: {} ", e.getMessage());
        return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
    }

    /** Handle RazorpayException globally.
     *
     * @param e the RazorpayException
     * @return ResponseEntity with ErrorResponse DTO and HTTP status 400
     */
    @ExceptionHandler(RazorpayException.class)
    public ResponseEntity<ErrorResponse> razorpayExceptionHandler(RazorpayException e){
        ErrorResponse errorResponse = new ErrorResponse("Razorpay payment error", e.getMessage());
        log.error("Razorpay exception: {}", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    /** Handle ServiceNotAvailableException globally.
     *
     * @param e the ServiceNotAvailableException
     * @return ResponseEntity with error message and HTTP status 503
     */
    @ExceptionHandler(ServiceNotAvailableException.class)
    public ResponseEntity<ErrorResponse> serviceNotAvailableExceptionHandler(ServiceNotAvailableException e){
        ErrorResponse errorResponse = new ErrorResponse("Service Not Available", e.getCustomerLandingPageMessage());
        log.error("Service not available exception: {}", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }


    /** Handle general Exception globally.
     *
     * @param e the Exception
     * @return ResponseEntity with error message and HTTP status 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> generalExceptionHandler(Exception e){
        log.error("General exception: {}", e.getMessage());
        return new ResponseEntity<>("Error - "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
