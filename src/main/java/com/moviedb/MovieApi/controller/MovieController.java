package com.moviedb.MovieApi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviedb.MovieApi.dto.MovieDto;
import com.moviedb.MovieApi.dto.MoviePageResponse;
import com.moviedb.MovieApi.exceptions.EmptyFileException;
import com.moviedb.MovieApi.service.MovieService;
import com.moviedb.MovieApi.utils.AppConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movie")
public class MovieController {

    private final MovieService movieService;


    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add-movie")
    public ResponseEntity<MovieDto> addMovieHandler(@RequestPart MultipartFile file, @RequestPart String movieDto ) throws JsonProcessingException, IOException, EmptyFileException {
        if(file.isEmpty()){
            throw new EmptyFileException("File cannot be Empty!!");
        }
     MovieDto dto =   convertToMovieDtoObject(movieDto);
        return new ResponseEntity<>(movieService.addMovie(dto, file), HttpStatus.CREATED);
    }

    @GetMapping("/{movieId}")
    public  ResponseEntity<MovieDto> getMovieHandler(@PathVariable Integer movieId){
       return ResponseEntity.ok(movieService.getMovie(movieId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MovieDto>> getAllMoviesHandler(){
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @PutMapping("/update/{movieId}")
    public ResponseEntity<?> updateMovie(@PathVariable Integer movieId,@RequestPart MultipartFile file, @RequestPart String movieDto ) throws IOException {
        if(file.isEmpty()) file = null;
        MovieDto mv = convertToMovieDtoObject(movieDto);
        return ResponseEntity.ok(movieService.updateMovie(movieId,mv,file));
    }
    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<String> deleteMovie(@PathVariable Integer movieId) throws IOException {
        String response = movieService.deleteMovie(movieId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/allMoviesPage")
    public ResponseEntity<MoviePageResponse> getMoviesWithPagination(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize
    ){
        return ResponseEntity.ok(movieService.getAllMoviesWithPagination(pageNumber,pageSize));
    }

    @GetMapping("/allMoviesPageSort")
    public ResponseEntity<MoviePageResponse> getMoviesWithPaginationAndSorting(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_BY,required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIR,required = false) String dir
    ){
        return ResponseEntity.ok(movieService.getAllMoviesWithPaginationAndSorting(pageNumber,pageSize,sortBy,dir));
    }

    private MovieDto convertToMovieDtoObject(String movieDtoObj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(movieDtoObj, MovieDto.class);
    }
}
