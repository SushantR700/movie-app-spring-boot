package com.moviedb.MovieApi.auth.controllers;

import com.moviedb.MovieApi.auth.entities.RefreshToken;
import com.moviedb.MovieApi.auth.entities.User;
import com.moviedb.MovieApi.auth.service.AuthService;
import com.moviedb.MovieApi.auth.service.JwtService;
import com.moviedb.MovieApi.auth.service.RefreshTokenService;
import com.moviedb.MovieApi.auth.utils.AuthResponse;
import com.moviedb.MovieApi.auth.utils.LoginRequest;
import com.moviedb.MovieApi.auth.utils.RefreshTokenRequest;
import com.moviedb.MovieApi.auth.utils.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
        private final AuthService authService;
        private final RefreshTokenService refreshTokenService;
        private final JwtService jwtService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest){
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());
        User user = refreshToken.getUser();
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken.getRefreshToken())
                .build());
    }


}
