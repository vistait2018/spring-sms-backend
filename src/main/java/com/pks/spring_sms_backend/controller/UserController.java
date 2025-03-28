package com.pks.spring_sms_backend.controller;


import com.pks.spring_sms_backend.entity.User;
import com.pks.spring_sms_backend.model.CreateUserModel;
import com.pks.spring_sms_backend.model.EmailModel;
import com.pks.spring_sms_backend.model.LoginModel;
import com.pks.spring_sms_backend.model.UserResponse;
import com.pks.spring_sms_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid  @RequestBody CreateUserModel createUser){

        return userService.register(createUser);

    }


    @PostMapping("/change-email")
    public ResponseEntity<UserResponse>
    changeEmail(@Valid @RequestBody EmailModel emailModel){
      return new ResponseEntity<>(userService.changeEmail(emailModel), HttpStatus.OK);
    }


    @GetMapping("/logged-in-user")
    public ResponseEntity<UserResponse> getLoggedInUser(){
        return new ResponseEntity<>(userService.getLoggedInUser(),HttpStatus.OK);
    }
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUser(){
       return new ResponseEntity<>(userService.getAllUser(),HttpStatus.OK) ;
    }
    @PostMapping("/login")
    public String login(@RequestBody LoginModel loginModel){
        return userService.login(loginModel);
    }
}
