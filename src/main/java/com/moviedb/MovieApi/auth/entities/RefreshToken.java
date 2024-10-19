package com.moviedb.MovieApi.auth.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue
    private Integer tokenId;
    @Column(nullable = false,length = 500)
    @NotBlank(message = "Please enter refresh token value")
    private String refreshToken;
    @Column(nullable = false)
    private Instant expirationTime;
    @OneToOne
    private User user;
}
