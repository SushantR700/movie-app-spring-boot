package com.moviedb.MovieApi.exceptions;

public class EmptyFileException extends Throwable {
    public EmptyFileException(String message) {
        super(message);
    }
}
