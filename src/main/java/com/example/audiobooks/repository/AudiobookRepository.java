package com.example.audiobooks.repository;

import com.example.audiobooks.entity.Audiobook;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AudiobookRepository extends JpaRepository<Audiobook, Long> {

    @Query("""
        SELECT a, ua
        FROM Audiobook a
        LEFT JOIN UserAudiobook ua
            ON a.id = ua.audiobook.id
            AND ua.user.id = :userId
        WHERE ua.isHidden IS NULL OR ua.isHidden = false
        ORDER BY a.id ASC
    """)
    List<Object[]> findAllCatalogItemsForUser(@Param("userId") Long userId);

    @Query("""
        SELECT a
        FROM Audiobook a
        LEFT JOIN UserAudiobook ua
            ON a.id = ua.audiobook.id
            AND ua.user.id = :userId
        WHERE a.id = :audiobookId
          AND (ua.isHidden IS NULL OR ua.isHidden = false)
    """)
    Optional<Audiobook> findAudiobookForUser(@Param("audiobookId") Long audiobookId, @Param("userId") Long userId);
}