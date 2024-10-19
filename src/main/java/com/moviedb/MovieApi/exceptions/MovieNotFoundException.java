package com.moviedb.MovieApi.exceptions;

public class MovieNotFoundException extends RuntimeException{
    public MovieNotFoundException(String message){
            super(message);
    }
}
