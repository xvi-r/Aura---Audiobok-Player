package com.example.audiobooks.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.audiobooks.entity.UserAudiobook;

public interface UserAudiobookRepository extends JpaRepository<UserAudiobook, Long> {
     List<UserAudiobook> findAllByUserId(Long id);
     List<UserAudiobook> findAllByUserIdOrderByIdAsc(Long id);
     Optional<UserAudiobook> findByUserIdAndAudiobookId(Long userId, Long AudiobookId);
     List<UserAudiobook> findAllByUserIdOrderByAudiobookIdAsc(Long userId);

     Optional<UserAudiobook> findFirstByUserIdAndLastPlayedAtIsNotNullOrderByLastPlayedAtDesc(Long userId);
     @Query("""
          SELECT ua FROM UserAudiobook ua
          WHERE ua.user.id = :userId
               AND ua.lastPlayedAt IS NOT NULL
               AND (ua.isHidden IS NULL OR ua.isHidden = false)
          ORDER BY ua.lastPlayedAt DESC
          LIMIT 8
     """)
     List<UserAudiobook> findTop8ContinueListeningForUser(@Param("userId") Long userId);
}
