package com.pks.spring_sms_backend.entity;

import com.pks.spring_sms_backend.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class User {

    @Id
    @SequenceGenerator(
            name="user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    private Long userId;

    @NotEmpty(message = "Email field cannot be empty")
    @Email(message = "Email field is not valid")
    @Size(max = 255, message = "Email field cannot be more than 255 characters")
    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    @NotEmpty(message = "Password field cannot be empty")
    private String password;

    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status;

    @ManyToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "school_id",
            referencedColumnName = "schoolId"
    )
    @ToString.Exclude
    private School school;

    @ManyToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(
                    name = "user_id",
                    referencedColumnName = "userId"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id",
                    referencedColumnName = "roleId"
            )
    )
    @ToString.Exclude
    private List<Role> roles ;

    public void addRole(Role role) {
        if(roles == null ) roles = new ArrayList<>();
        roles.add(role);
    }

    @PrePersist
    @PreUpdate
    private void prepareData() {
        if (this.email != null) {
            this.email = email.toLowerCase();
        }
    }
}
