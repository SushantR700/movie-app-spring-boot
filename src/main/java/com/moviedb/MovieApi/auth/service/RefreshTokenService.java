package com.moviedb.MovieApi.auth.service;

import com.moviedb.MovieApi.auth.entities.RefreshToken;
import com.moviedb.MovieApi.auth.entities.User;
import com.moviedb.MovieApi.auth.repositories.RefreshTokenRepository;
import com.moviedb.MovieApi.auth.repositories.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(String email){
     User user =   userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found with email:" + email));

     RefreshToken refreshToken = user.getRefreshToken();
        long refreshTokenValidity = 30*1000;
     if(refreshToken == null){
         refreshToken = RefreshToken.builder()
                 .refreshToken(UUID.randomUUID().toString())
                 .expirationTime(Instant.now().plusMillis(refreshTokenValidity))
                 .user(user).build();
         refreshTokenRepository.save(refreshToken);
     }
     return  refreshToken;
    }

    public RefreshToken verifyRefreshToken(String refreshToken){
     RefreshToken refreshTokenResponse =   refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(()-> new RuntimeException("Token not found"));

     if(refreshTokenResponse.getExpirationTime().compareTo(Instant.now()) < 0){
        refreshTokenRepository.delete(refreshTokenResponse);
        throw new RuntimeException("Refresh Token Expired");
     }
     return refreshTokenResponse;
    }
}
