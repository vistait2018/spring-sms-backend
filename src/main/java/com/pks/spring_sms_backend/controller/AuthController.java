package com.pks.spring_sms_backend.controller;

import com.pks.spring_sms_backend.model.CreateUserModel;
import com.pks.spring_sms_backend.model.LoginModel;
import com.pks.spring_sms_backend.model.UserResponse;
import com.pks.spring_sms_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody CreateUserModel createUser){

        return userService.register(createUser);
    }
    @PostMapping("/login")
    public String login(@RequestBody LoginModel loginModel){
        return userService.login(loginModel);
    }
}
