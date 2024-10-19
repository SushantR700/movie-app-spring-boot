package com.moviedb.MovieApi.service;

import com.moviedb.MovieApi.dto.MovieDto;
import com.moviedb.MovieApi.dto.MoviePageResponse;
import com.moviedb.MovieApi.entity.Movie;
import com.moviedb.MovieApi.exceptions.FileExistsException;
import com.moviedb.MovieApi.exceptions.MovieNotFoundException;
import com.moviedb.MovieApi.repo.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService{

    private  final MovieRepository movieRepository;
    private final FileService fileService;

    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String BASEURl;

    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }

    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {

        //Check if filename already exists
        if(Files.exists(Paths.get(path+"/"+file.getOriginalFilename()))){
            throw new FileExistsException("File name already exist , please enter new file name");

        }

       // 1. upload the file
           String uploadedFileName = fileService.uploadFile(path,file);
       // 2. set the value of field 'poster' as file name
        movieDto.setPoster(uploadedFileName);
        // 3. map dto to movie object
        Movie movie = new Movie(
                null,
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );
        // 4. save the movie object -> Saved movie object
        Movie savedMovie = movieRepository.save(movie);

        // 5. generate the posterurl
            String posterUrl = BASEURl+"/file/"+uploadedFileName;
        // 6. Map movie object to dto object and return it
                MovieDto response = new MovieDto(
                  savedMovie.getMovieId(),
                  savedMovie.getTitle(),
                  savedMovie.getDirector(),
                  savedMovie.getStudio(),
                  savedMovie.getMovieCast(),
                  savedMovie.getReleaseYear(),
                  savedMovie.getPoster(),
                  posterUrl
                );
        return response;
    }

    @Override
    public MovieDto getMovie(Integer movieId) {
        // 1. Check the data in DB and if exists, fetch the data of given ID
       Movie movie = movieRepository.findById(movieId).orElseThrow(()-> new MovieNotFoundException("Movie not found"));

        // 2. Generate postUrl
        String posterURl = BASEURl + "/file/" + movie.getPoster();

        // 3. Map to MovieDto object and return it
        MovieDto movieDto = new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterURl
        );

        return movieDto;
    }

    @Override
    public List<MovieDto> getAllMovies() {
        // 1. fetch all data from db
        List<Movie> movies = movieRepository.findAll();

        List<MovieDto> movieDtos = new ArrayList<>();
        //2. iterate through the list, generate posterurl for each movie obj,
        // and map to movieDto obj
        for(Movie movie:movies){
            String posterURl = BASEURl + "/file/"+movie.getPoster();
            MovieDto movieDto = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterURl
            );
            movieDtos.add(movieDto);
        }

        return movieDtos;
    }

    @Override
    public MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException {
        // 1. Check if the movie exits or not , if not throw exception
          Movie mv = movieRepository.findById(movieId).orElseThrow(()-> new RuntimeException("No movie found for this ID"));

        // 2. if file is null then do nothing otherwise delete existing file associated with the record and upload the new file
        String fileName = mv.getPoster();
            if(file != null){
                Files.deleteIfExists(Paths.get(path + File.separator + fileName));
                fileName = fileService.uploadFile(path,file);
            }

        // 3. Set the poster value according to step 2
                movieDto.setPoster(fileName);

        // 4. Map to movie object
                Movie movie = new Movie(
                        mv.getMovieId(),
                        movieDto.getTitle(),
                        movieDto.getDirector(),
                        movieDto.getStudio(),
                        movieDto.getMovieCast(),
                        movieDto.getReleaseYear(),
                        movieDto.getPoster()
                );

        // 5. Save the movie object, and it will return the movie object
               Movie updatedMovie = movieRepository.save(movie);

        // 6. generate poster url for it
                String posterUrl = BASEURl + File.separator + fileName;

        // 7. map and return the movie-dto object
                MovieDto response = new MovieDto(
                        updatedMovie.getMovieId(),
                        updatedMovie.getTitle(),
                        updatedMovie.getDirector(),
                        updatedMovie.getStudio(),
                        updatedMovie.getMovieCast(),
                        updatedMovie.getReleaseYear(),
                        updatedMovie.getPoster(),
                        posterUrl
                );
                return response;
    }

    @Override
    public String deleteMovie(Integer movieId) throws IOException {
        // 1. Check if the movie for given id is present or not
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("No movie found for this id"));
        // 2. If present, delete the file associated with it and also delete the movie from database
        String fileName = movie.getPoster();
        System.out.println("Movie URl : " + path +"/"+ fileName);

        Files.deleteIfExists(Paths.get(path +"/"+ fileName));
        movieRepository.deleteById(movieId);
        return "SuccessFully Deleted";
    }

    @Override
    public MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        Page<Movie> moviePage = movieRepository.findAll(pageable);
        List<Movie> movies = moviePage.getContent();

        List<MovieDto> movieDtos = new ArrayList<>();
        //2. iterate through the list, generate posterurl for each movie obj,
        // and map to movieDto obj
        for(Movie movie:movies){
            String posterURl = BASEURl + "/file/"+movie.getPoster();
            MovieDto movieDto = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterURl
            );
            movieDtos.add(movieDto);
        }
        return new MoviePageResponse(movieDtos,pageNumber,pageSize,moviePage.getTotalElements(),moviePage.getTotalPages(),moviePage.isLast());
    }

    @Override
    public MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String dir) {
        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<Movie> moviePage = movieRepository.findAll(pageable);
        List<Movie> movies = moviePage.getContent();

        List<MovieDto> movieDtos = new ArrayList<>();
        //2. iterate through the list, generate posterurl for each movie obj,
        // and map to movieDto obj
        for(Movie movie:movies){
            String posterURl = BASEURl + "/file/"+movie.getPoster();
            MovieDto movieDto = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterURl
            );
            movieDtos.add(movieDto);
        }
        return new MoviePageResponse(movieDtos,pageNumber,pageSize,moviePage.getTotalElements(),moviePage.getTotalPages(),moviePage.isLast());
    }
}
