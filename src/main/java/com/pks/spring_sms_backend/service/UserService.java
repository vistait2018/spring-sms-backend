package com.pks.spring_sms_backend.service;


import com.pks.spring_sms_backend.entity.Role;
import com.pks.spring_sms_backend.enums.UserStatus;
import com.pks.spring_sms_backend.exception.RoleNotFoundException;
import com.pks.spring_sms_backend.model.*;
import com.pks.spring_sms_backend.repository.RoleRepository;
import com.pks.spring_sms_backend.repository.UserRepository;
import com.pks.spring_sms_backend.entity.User;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
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
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Log4j2
public class UserService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JWTService jwtService;


    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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

    public Page<UserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreated").descending());

        Page<User> usersPage = userRepository.findAll(pageable);

        return usersPage.map(user ->
                UserResponse.builder()
                        .userId(user.getUserId())
                        .email(user.getEmail())
                        .dateCreated(user.getDateCreated())
                        .status(user.getStatus())
                        .dateUpdated(user.getDateUpdated())
                        .roles(user.getRoles())
                        .build()
        );
    }

    @Transactional
    public UserResponse addRolesToUser(Long userId, List<AddRole> roles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User with id " + userId + " Not found"));

        // Convert current roles to a Set for fast lookup
        Set<String> existingRoleNames = user.getRoles().stream()
                .map(role -> role.getRoleName().toLowerCase())
                .collect(Collectors.toSet());

        // Get only new roles that user doesn't have
        List<Role> newRoles = roleRepository.findAll().stream()
                .filter(role -> !existingRoleNames.contains(role.getRoleName().toLowerCase()))
                .filter(role -> roles.stream()
                        .map(AddRole::getRoleName)
                        .map(String::toLowerCase)
                        .collect(Collectors.toSet())
                        .contains(role.getRoleName().toLowerCase()))
                .collect(Collectors.toList());

        // Add only new roles to the user
        user.getRoles().addAll(newRoles);
        User updatedUser = userRepository.save(user);

        return UserResponse.builder()
                .userId(updatedUser.getUserId())
                .email(updatedUser.getEmail())
                .status(updatedUser.getStatus())
                .roles(updatedUser.getRoles())
                .dateCreated(updatedUser.getDateCreated())
                .dateUpdated(updatedUser.getDateUpdated())
                .build();
    }

    @Transactional
    public UserResponse addRoleToUser(Long userId, Long roleId) {
        User user = getUserById(userId);
        Role roleToAdd = getRoleById(roleId);

        if (!userHasRole(user, roleToAdd)) {
            user.getRoles().add(roleToAdd);
            userRepository.save(user); // Save only if a new role is added
        }

        return buildUserResponse(user);
    }
    public UserResponse findUserById(Long userId){
       return buildUserResponse(getUserById(userId));
    }
    @Transactional
    public UserResponse removeRoleFromUser(Long userId, Long roleId, String requestingUserEmail) {
        User user = getUserById(userId);
        Role roleToRemove = getRoleById(roleId);

        if (!userHasRole(user, roleToRemove)) {
            throw new IllegalStateException("User does not have the role: " + roleToRemove.getRoleName());
        }

        // Prevent removing SUPER_ADMIN unless done by another SUPER_ADMIN
        if (roleToRemove.getRoleName().equalsIgnoreCase("SUPER_ADMIN")) {
            validateSuperAdminPrivilege(requestingUserEmail);
        }

        user.getRoles().remove(roleToRemove);
        userRepository.save(user);

        return buildUserResponse(user);
    }

    /** 🔹 Helper Methods for Code Reusability */
   // @PreAuthorize("hasRole('ROLE_USER')")
    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User with id " + userId + " Not found"));
    }

    private Role getRoleById(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role with id " + roleId + " not found"));
    }

    private boolean userHasRole(User user, Role role) {
        return user.getRoles().stream()
                .anyMatch(existingRole ->
                        existingRole.getRoleName().equalsIgnoreCase(role.getRoleName())
                );
    }


    private void validateSuperAdminPrivilege(String requestingUserEmail) {
        User requestingUser = userRepository.findByEmail(requestingUserEmail);
        if (requestingUser == null) {
            throw new UsernameNotFoundException("Requesting user not found");
        }

        boolean isSuperAdmin = requestingUser.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equalsIgnoreCase("ROLE_SUPER_ADMIN"));

        if (!isSuperAdmin) {
            throw new AccessDeniedException("Access denied " +
                    "");
        }
    }

    private UserResponse buildUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .status(user.getStatus())
                .roles(user.getRoles())
                .dateCreated(user.getDateCreated())
                .dateUpdated(user.getDateUpdated())
                .build();
    }


}
