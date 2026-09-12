package com.example.audiobooks.service;

import com.example.audiobooks.repository.AudiobookRepository;
import com.example.audiobooks.repository.UserAudiobookRepository;
import com.example.audiobooks.repository.UserRepository;
import com.example.audiobooks.security.CustomUserDetails;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.example.audiobooks.mapper.AudiobookMapper;
import com.example.audiobooks.mapper.UserAudiobookMapper;
import com.example.audiobooks.dto.audiobook.AudiobookResponse;
import com.example.audiobooks.dto.audiobook.CatalogAudiobookResponse;
import com.example.audiobooks.dto.userAudiobook.AudiobookProgressRequest;
import com.example.audiobooks.dto.userAudiobook.UserAudiobookProgressResponse;
import com.example.audiobooks.dto.userAudiobook.UserAudiobookResponse;
import com.example.audiobooks.entity.Audiobook;
import com.example.audiobooks.entity.UserAudiobook;
import com.example.audiobooks.entity.Series;
import com.example.audiobooks.entity.User;
import com.example.audiobooks.exception.NoPlayedAudiobookException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@RequiredArgsConstructor
@Slf4j
@Service
public class UserAudiobookService {
        private final UserAudiobookRepository userAudiobookRepository;
        private final UserAudiobookMapper userAudiobookMapper;
        private final AudiobookRepository audiobookRepository;
        private final AudiobookMapper audiobookMapper;
        private final UserRepository userRepository;

    public List<CatalogAudiobookResponse> getUserAudiobooks(Long id) {
        log.debug("Fetching user audiobooks shelf for userId={}", id);
        List<Object[]> audiobookAndUserAudiobook = audiobookRepository.findAllCatalogItemsForUser(id);

        return  audiobookAndUserAudiobook.stream().map(audiobookMapper::toResponse).toList();

      
        //         .map(userAudiobookMapper::toResponse)
        //         .toList();

        //return unhiddenAudiobooksForUser.stream().map(null)
    
        //return unhiddenAudiobooksForUser;

    }

    public UserAudiobookProgressResponse updateProgress(Long userId, Long audiobookId,
            AudiobookProgressRequest audiobookProgressRequest) {
         UserAudiobook userAudiobook = userAudiobookRepository.findByUserIdAndAudiobookId(userId, audiobookId)
            .orElseGet(() -> {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                Audiobook audiobook = audiobookRepository.findById(audiobookId)
                        .orElseThrow(() -> new RuntimeException("Audiobook not found"));
                UserAudiobook newRecord = new UserAudiobook();
                newRecord.setUser(user);
                newRecord.setAudiobook(audiobook);
                newRecord.setPosition(0.0);
                newRecord.setCompleted(false);
                return newRecord;
            });

        userAudiobook.setPosition(audiobookProgressRequest.getPosition());
        userAudiobook.setCompleted(audiobookProgressRequest.isCompleted());
        userAudiobook.setLastPlayedAt(Instant.now());

        userAudiobookRepository.save(userAudiobook);

        log.info("Updated playback progress: userId={}, audiobookId={}, position={}s, completed={}",
                userId, audiobookId, audiobookProgressRequest.getPosition(), audiobookProgressRequest.isCompleted());

        return new UserAudiobookProgressResponse(userAudiobook.getPosition(), userAudiobook.isCompleted(),
                Instant.now());
    }

    public UserAudiobookProgressResponse getProgressForAudiobook(Long userId, Long audiobookId) {

        UserAudiobook userAudiobook = userAudiobookRepository.findByUserIdAndAudiobookId(userId, audiobookId)
                .orElseThrow(() -> new RuntimeException("UserAudiobook Not Found"));

        UserAudiobookProgressResponse response = new UserAudiobookProgressResponse();

        response.setPosition(userAudiobook.getPosition());
        response.setCompleted(userAudiobook.isCompleted());
        response.setUpdatedAt(userAudiobook.getLastPlayedAt());

        log.info("User resumed/started audiobook: userId={}, audiobookId={}, position={}s, completed={}",
                userId, audiobookId, response.getPosition(), response.isCompleted());

        return response;
    }

    public UserAudiobookResponse getMostRecentAudiobook(Long userId) {
        UserAudiobook userAudiobook = userAudiobookRepository.findFirstByUserIdAndLastPlayedAtIsNotNullOrderByLastPlayedAtDesc(userId)
                .orElseThrow(() -> new NoPlayedAudiobookException("User has not played any audiobooks"));

        log.debug("Fetched most recent audiobook for userId={}: audiobookId={}", userId, userAudiobook.getAudiobook().getId());
        return userAudiobookMapper.toResponse(userAudiobook);
    }
    
    public List<UserAudiobookResponse> continueListening(Long userId) {
        log.debug("Fetching continue-listening shelf for userId={}", userId);
        List<UserAudiobook> userAudiobooks = userAudiobookRepository.findTop8ContinueListeningForUser(userId);

        return userAudiobooks.stream()
                .map(userAudiobookMapper::toResponse)
                .toList();
    }
}
