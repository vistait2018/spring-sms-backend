package com.pks.spring_sms_backend.service;


import com.pks.spring_sms_backend.enums.UserStatus;
import com.pks.spring_sms_backend.model.LoginModel;
import com.pks.spring_sms_backend.model.UserResponse;
import com.pks.spring_sms_backend.repository.UserRepository;
import com.pks.spring_sms_backend.entity.User;

import com.pks.spring_sms_backend.model.CreateUserModel;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Log4j2
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JWTService jwtService;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }


    public ResponseEntity<UserResponse> register(CreateUserModel createUserModel){
        createUserModel.setDateCreated(LocalDateTime.now());
        createUserModel.setDateUpdated(LocalDateTime.now());
        createUserModel.setStatus(UserStatus.ACTIVE);
        User userToSave = User
                .builder()
                .email(createUserModel.getEmail())
                .password(passwordEncoder.encode(createUserModel.getPassword()))
                .dateCreated(createUserModel.getDateCreated())
                .dateUpdated(createUserModel.getDateUpdated())
                .status(createUserModel.getStatus())
                .build();
        User saved = userRepository.save(userToSave);
        final UserResponse userResponse = UserResponse.builder()
                .userId(saved.getUserId())
                .email(saved.getEmail())
                .dateCreated(saved.getDateCreated())
                .dateUpdated(saved.getDateUpdated())
                .status(saved.getStatus())
                .build();
        return new ResponseEntity<>(userResponse, HttpStatus.OK);


    }

    public String login(LoginModel loginModel){
        final Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginModel.getEmail(), loginModel.getPassword()
                ));
         log.info("Trying to log in email "+ loginModel.getEmail() + " password "+ loginModel.getPassword());
        if(!authenticate.isAuthenticated())
            return "error";
        else return jwtService.generateToken(loginModel);
    }



}
