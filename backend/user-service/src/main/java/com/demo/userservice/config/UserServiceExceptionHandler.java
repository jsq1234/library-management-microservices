package com.demo.userservice.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.amazonaws.services.cognitoidp.model.CodeMismatchException;
import com.amazonaws.services.cognitoidp.model.UserNotFoundException;
import com.demo.userservice.dto.HttpError;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class UserServiceExceptionHandler {
    @ExceptionHandler(CodeMismatchException.class)
    public ResponseEntity<HttpError> handleInvalidConfirmationCodeError(CodeMismatchException ex){
        log.info("{}", ex.getMessage());
        return ResponseEntity.badRequest().body(new HttpError(HttpStatus.BAD_REQUEST, "Invalid confirmation code."));
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpError> handleUserNotFoundException(UserNotFoundException ex){
        log.info("{}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new HttpError(HttpStatus.UNAUTHORIZED, "User not found."));
    }
}
