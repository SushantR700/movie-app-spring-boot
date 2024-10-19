package com.moviedb.MovieApi.auth.utils;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
}
