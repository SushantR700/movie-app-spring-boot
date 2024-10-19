package com.moviedb.MovieApi.repo;

import com.moviedb.MovieApi.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie,Integer> {
}
