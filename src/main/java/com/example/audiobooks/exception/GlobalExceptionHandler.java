package com.example.audiobooks.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import jakarta.persistence.EntityNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(UserNameAlreadyExistsException.class)
        public ResponseEntity<String> handleUsernameAlreadyExists(
                        UserNameAlreadyExistsException ex) {
                log.warn("Registration conflict: {}", ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(ex.getMessage());
        }

        @ExceptionHandler(NoPlayedAudiobookException.class)
        public ResponseEntity<String> handleNoPlayedAudiobook(
                        NoPlayedAudiobookException ex) {
                log.info("No played audiobook found: {}", ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(ex.getMessage());
        }

        @ExceptionHandler(HttpClientErrorException.NotFound.class)
        public ResponseEntity<String> handleAsinNotFound(
                        HttpClientErrorException.NotFound e) {
                log.warn("ASIN metadata not found: {}", e.getMessage());
                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body("ASIN does not exist");
        }

        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
                log.warn("Entity not found: {}", ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(ex.getMessage());
        }

        @ExceptionHandler(AudiobookNotFoundException.class)
        public ResponseEntity<String> handleAudiobookNotFound(
                        AudiobookNotFoundException ex) {
                log.warn("Audiobook not found: {}", ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(ex.getMessage());
        }
}
