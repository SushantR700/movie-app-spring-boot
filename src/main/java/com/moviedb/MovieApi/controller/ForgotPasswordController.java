package com.moviedb.MovieApi.controller;

import com.moviedb.MovieApi.auth.entities.User;
import com.moviedb.MovieApi.auth.repositories.UserRepository;
import com.moviedb.MovieApi.dto.MailBody;
import com.moviedb.MovieApi.entity.ForgotPassword;
import com.moviedb.MovieApi.repo.ForgotPasswordRepo;
import com.moviedb.MovieApi.service.EmailService;
import com.moviedb.MovieApi.utils.ChangePassword;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/forgotPassword")
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private  final ForgotPasswordRepo forgotPasswordRepo;

    private final PasswordEncoder passwordEncoder;

    //Send mail for email verification
    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email){
        User user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("Please provide a valid email"));
        int otp = otpGenerator();
        MailBody mailBody = MailBody.builder().to(email).text("This is the otp for your Forgot Password" + otp)
                .subject("OTP for forgot Password Request").build();

        ForgotPassword fp = ForgotPassword.builder().otp(otp).expirationTime(new Date(System.currentTimeMillis() + 70 * 10000))
                .user(user).build();
            emailService.sendSimpleMessage(mailBody);
            forgotPasswordRepo.save(fp);
            return ResponseEntity.ok("Email sent for verification");
    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("Please provide a valid email"));
       ForgotPassword fp = forgotPasswordRepo.findByOtpAndUser(otp,user).orElseThrow(()-> new RuntimeException("Invalid OTP"));

        if(fp.getExpirationTime().before(Date.from(Instant.now()))){
            forgotPasswordRepo.deleteById(fp.getFpid());
            return new ResponseEntity<>("OTP has expired", HttpStatus.EXPECTATION_FAILED);
        }
        return ResponseEntity.ok("Successfully verified");
    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(@RequestBody ChangePassword changePassword, @PathVariable String email){
        if(!Objects.equals(changePassword.password(),changePassword.confirmPassword())){
            return new ResponseEntity<>("Please enter the password again!", HttpStatus.BAD_REQUEST);
        }
        String encodedPassword = passwordEncoder.encode(changePassword.password());
        userRepository.updatePassword(email,encodedPassword);
        return ResponseEntity.ok("Password has been changes Successfully!");
    }

    private Integer otpGenerator(){
        Random random = new Random();
        return random.nextInt(100000,999999);
    }
}
