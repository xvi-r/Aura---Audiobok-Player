package com.example.audiobooks.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileStorageServiceTest {

    private final FileStorageService fileStorageService = new FileStorageService();

    @Test
    @DisplayName("delete - Should actually delete folder and its contents from disk")
    void delete_shouldDeleteFolderAndContentsFromDisk(@TempDir Path tempDir) throws IOException {
        // 1. Arrange - Create a real temporary folder & file on disk
        Path audiobookFolder = tempDir.resolve("audiobook_1");
        Files.createDirectories(audiobookFolder);
        Path sampleAudioFile = audiobookFolder.resolve("audio.m4b");
        Files.writeString(sampleAudioFile, "dummy audio content");

        assertThat(Files.exists(sampleAudioFile)).isTrue();

        // 2. Act - Call real fileStorageService.delete() on real disk path
        fileStorageService.delete(audiobookFolder.toString());

        // 3. Assert - Prove the file & folder were ACTUALLY deleted from disk
        assertThat(Files.exists(audiobookFolder)).isFalse();
        assertThat(Files.exists(sampleAudioFile)).isFalse();
    }
}
