package dev.siraj.restauron.exceptionHandlers;

import dev.siraj.restauron.exceptionHandlers.errorResponseDto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> generalExceptionHandler(Exception e){


        return new ResponseEntity<>("Error - "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
