package com.moviedb.MovieApi.dto;

import java.util.List;

public record MoviePageResponse(List<MovieDto> movieDtoList, Integer pageeNumber, Integer pageSize,
long totalElements, int totalPages, boolean isLast) {
}
