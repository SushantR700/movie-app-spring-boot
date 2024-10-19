package com.moviedb.MovieApi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movie")
public class Movie {
    @Id
    @GeneratedValue
    private Integer movieId;

    @Column(nullable = false)
    @NotBlank(message = "Please provide movie's title")
    private String title;
    @Column(nullable = false)
    @NotBlank(message = "Please provide director's title")
    private String director;

    @Column(nullable = false)
    @NotBlank(message = "Please provide studio's title")
    private String studio;

    @ElementCollection
    @CollectionTable(name = "movie_cast")
    private Set<String> movieCast;

    @Column(nullable = false)
    private Integer releaseYear;

    @Column(nullable = false)
    @NotBlank(message = "Please provide poster's title")
    private String poster;
}
