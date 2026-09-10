package com.example.audiobooks.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "application_state")
@Getter
@Setter
public class ApplicationStateEntity {

    @Id
    private Long id;

    private boolean setupCompleted;
}
