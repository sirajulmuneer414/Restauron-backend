package dev.siraj.restauron.exceptionHandlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerTwo {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandlerTwo() {
        return new ResponseEntity<>("Exception from app", HttpStatus.BAD_REQUEST);
    }


}
