package com.example.audiobooks.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.audiobooks.entity.ApplicationStateEntity;

public interface ApplicationStateRepository
        extends JpaRepository<ApplicationStateEntity, Long> {
}
