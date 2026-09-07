package com.example.audiobooks.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(UserNameAlreadyExistsException.class)
        public ResponseEntity<String> handleUsernameAlreadyExists(
                        UserNameAlreadyExistsException ex) {

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(ex.getMessage());
        }

        @ExceptionHandler(NoPlayedAudiobookException.class)
        public ResponseEntity<String> handleNoPlayedAudiobook(
                        NoPlayedAudiobookException ex) {

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(ex.getMessage());
        }

        @ExceptionHandler(HttpClientErrorException.NotFound.class)
        public ResponseEntity<String> handleAsinNotFound(
                        HttpClientErrorException.NotFound e) {

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body("ASIN does not exist");
        }

        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(ex.getMessage());
        }

        @ExceptionHandler(AudiobookNotFoundException.class)
        public ResponseEntity<String> handleAudiobookNotFound(
                        AudiobookNotFoundException ex) {

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(ex.getMessage());
        }
}
