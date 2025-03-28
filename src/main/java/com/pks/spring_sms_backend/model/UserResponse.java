package com.pks.spring_sms_backend.model;

import com.pks.spring_sms_backend.entity.Role;
import com.pks.spring_sms_backend.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private Long userId;
    private String email;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private UserStatus status;
    private List<Role> roles;
}
