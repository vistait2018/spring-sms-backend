package com.pks.spring_sms_backend.controller;



import com.pks.spring_sms_backend.model.*;
import com.pks.spring_sms_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }



    //@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    @PostMapping("/change-email")
    public ResponseEntity<UserResponse>
    changeEmail(@Valid @RequestBody EmailModel emailModel){
      return new ResponseEntity<>(userService.changeEmail(emailModel), HttpStatus.OK);
    }


    @GetMapping("/logged-in-user")
    public ResponseEntity<UserResponse> getLoggedInUser(){
        return new ResponseEntity<>(userService.getLoggedInUser(),HttpStatus.OK);
    }
    //@PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public Page<UserResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return userService.getAllUsers(page, size);
    }


    @PostMapping("/all/{userId}/add-roles")
    public ResponseEntity<UserResponse>
    addRolesToUser(
          @Valid  @PathVariable Long userId,
            @RequestBody List<AddRole> roles
    ){
       return new ResponseEntity<>(
               userService.addRolesToUser(userId,roles),
               HttpStatus.OK) ;
    }

    @PostMapping("/all/{userId}/add-role/{roleId}")
    public ResponseEntity<UserResponse>
    addRoleToUser(
            @Valid  @PathVariable Long userId,
            @PathVariable Long roleId
    ){
        return new ResponseEntity<>(
                userService.addRoleToUser(userId,roleId),
                HttpStatus.OK) ;
    }

    @PostMapping("/all/{userId}/remove/{roleId}")
    public ResponseEntity<UserResponse> removeRoleFromUser(
            @PathVariable Long userId,
            @PathVariable Long roleId,
            @RequestBody String requestingUserEmail
    ){
       return new ResponseEntity<>(userService.removeRoleFromUser(userId,roleId, requestingUserEmail),HttpStatus.OK);
    }


    @GetMapping("/all/{userId}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long userId

    ){
        return new ResponseEntity<>(userService.findUserById(userId),HttpStatus.OK);
    }
}
