package com.example.audiobooks.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

@Service
public class FileStorageService {

    public void delete(String relativePath) {
    Path path = Path.of(relativePath);

    try (Stream<Path> paths = Files.walk(path)) {
        paths.sorted(Comparator.reverseOrder())
             .forEach(p -> {
                 try {
                     Files.delete(p);
                 } catch (IOException e) {
                     throw new UncheckedIOException(e);
                 }
             });
    } catch (IOException | UncheckedIOException e) {
        throw new RuntimeException("Failed to delete: " + path, e);
    }
}
}
