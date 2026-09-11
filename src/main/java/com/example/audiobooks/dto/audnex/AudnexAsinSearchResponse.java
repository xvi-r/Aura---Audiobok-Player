package com.example.audiobooks.dto.audnex;

import java.time.Instant;
import java.util.List;



public record AudnexAsinSearchResponse(
        String asin,
        List<Author> authors,
        Integer copyright,
        String description,
        String formatType,
        List<Genre> genres,
        String image,
        Boolean isAdult,
        String isbn,
        String language,
        String literatureType,
        List<Narrator> narrators,
        String publisherName,
        String rating,
        String region,
        Instant releaseDate,
        Integer runtimeLengthMin,
        String summary,
        String title
) {

    public record Author(
            String asin,
            String name
    ) {}

    public record Genre(
            String asin,
            String name,
            String type
    ) {}

    public record Narrator(
            String name
    ) {}
}
