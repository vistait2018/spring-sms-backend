package com.pks.spring_sms_backend.service;


import com.pks.spring_sms_backend.enums.UserStatus;
import com.pks.spring_sms_backend.model.EmailModel;
import com.pks.spring_sms_backend.model.LoginModel;
import com.pks.spring_sms_backend.model.UserResponse;
import com.pks.spring_sms_backend.repository.UserRepository;
import com.pks.spring_sms_backend.entity.User;

import com.pks.spring_sms_backend.model.CreateUserModel;

import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


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


    public UserResponse changeEmail(EmailModel emailModel) {
        User userWIthEmail = userRepository
                .findByEmail(emailModel.getOldEmail());

       if(userWIthEmail == null){
           throw new UsernameNotFoundException("User with Email " + emailModel.getOldEmail()+" not found");
       }
       userWIthEmail.setEmail(emailModel.getNewEmail());

       userWIthEmail.setDateUpdated(emailModel.getDateUpdated());
        final User saved = userRepository.save(userWIthEmail);
        return UserResponse.builder()
                .email(saved.getEmail())
                .dateUpdated(saved.getDateUpdated())
                .dateCreated(saved.getDateCreated())
                .userId(saved.getUserId())
                .status(saved.getStatus())
                .roles(saved.getRoles())
                .build();

    }


    public UserResponse getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            User user = userRepository.findByEmail(email);
            if(user == null){
                throw new UsernameNotFoundException("User Not found");
            }else{
                return UserResponse.builder()
                        .userId(user.getUserId())
                        .email(user.getEmail())
                        .status(user.getStatus())
                        .roles(user.getRoles())                                                                 .dateCreated(user.getDateCreated())
                        .dateUpdated(user.getDateUpdated())
                        .build();
            }

        }

        throw new RuntimeException("User is not authenticated");
    }

    public List<UserResponse> getAllUser(){
        return userRepository.findAll()
                .stream()
                .map(user->
                     UserResponse.builder()
                            .userId(user.getUserId())
                            .email(user.getEmail())
                            .dateCreated(user.getDateCreated())
                            .status(user.getStatus())
                            .dateUpdated(user.getDateUpdated())
                            .roles(user.getRoles())
                            .build()
                )
                .collect(Collectors.toList());
    }
}
