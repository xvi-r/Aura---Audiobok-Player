package com.example.audiobooks.exception;


public class AudiobookNotFoundException extends RuntimeException {

    public AudiobookNotFoundException(Long id) {
        super("Audiobook with id " + id + " was not found");
    }
}