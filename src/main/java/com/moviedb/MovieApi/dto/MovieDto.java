package com.moviedb.MovieApi.dto;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatusCode;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {
    private Integer movieId;
    @NotBlank(message = "Please provide movie's title")
    private String title;
    @NotBlank(message = "Please provide director's title")
    private String director;
    @NotBlank(message = "Please provide studio's title")
    private String studio;
    private Set<String> movieCast;
    private Integer releaseYear;
    @NotBlank(message = "Please provide poster's title")
    private String poster;
    @NotBlank(message = "Please provide poster's url")
    private String posterUrl;
}
