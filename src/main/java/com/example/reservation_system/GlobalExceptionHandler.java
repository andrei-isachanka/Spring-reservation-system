package com.example.reservation_system;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception e){
        log.error("Handle exception", e);

        var errorDTO = new ErrorResponseDTO("Internal Server Error", e.getMessage(), LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleEntityNotFoundException(Exception e){
        log.error("Handle entityNotFoundException", e);
        var errorDTO = new ErrorResponseDTO("Entity not found", e.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDTO);
    }

    @ExceptionHandler(exception = {IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ErrorResponseDTO> handleBadRequest(Exception e){
        log.error("Handle badRequestException", e);
        var errorDTO = new ErrorResponseDTO("Bad request", e.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }
}
