package com.example.audiobooks.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.example.audiobooks.dto.audnex.AudnexAsinSearchResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Service
public class DiscoverService {
    private final RestClient restClient;


    public AudnexAsinSearchResponse searchByAsin(String asin) {
        return restClient.get()
                .uri("https://api.audnex.us/books/{asin}?region=uk", asin)
                .retrieve()
                .body(AudnexAsinSearchResponse.class);
    }




}
