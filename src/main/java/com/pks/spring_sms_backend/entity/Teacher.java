package com.pks.spring_sms_backend.entity;


import com.pks.spring_sms_backend.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Teacher {


    @Id
    @SequenceGenerator(
            name="teacher_sequence",
            sequenceName = "teacher_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "teacher_sequence"
    )
    private Long teacherId;

    @NotNull(message = "first Name field cannot be null")
    @NotEmpty(message = "first Name field cannot be empty")
    private String firstName;

    @NotNull(message = "last Name field cannot be null")
    @NotEmpty(message = "last Name field cannot be empty")
    private String lastName;

    private String otherName;


    private LocalDate dob;

    @Size(min = 10, max = 10)
    @Pattern(regexp = "^\\d{10}$")
    private String phoneNo;

    @Email
    private String email;


    private String lId;

    @NotNull(message = "teacher address cannot be null")
    @NotEmpty(message = "teacher Address field cannot be empty")
    private String address;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDateTime dateCreated;


    private LocalDateTime dateUpdated;

    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(
            name="school_id",
            referencedColumnName = "schoolId"
    )
    private School school;
}
