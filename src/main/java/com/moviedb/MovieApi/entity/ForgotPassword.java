package com.moviedb.MovieApi.entity;

import com.moviedb.MovieApi.auth.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ForgotPassword {
    @Id
    @GeneratedValue
    private Integer fpid;
    @Column(nullable = false)
    private Integer otp;
    @Column(nullable = false)
    private Date expirationTime;
    @OneToOne
    private User user;
}
