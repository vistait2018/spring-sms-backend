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
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Student {

    @Id
    @SequenceGenerator(
            name="student_sequence",
            sequenceName = "student_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "student_sequence"
    )
    private Long studentId;

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

    @NotNull(message = "student address cannot be null")
    @NotEmpty(message = "student Address field cannot be empty")
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


    @ManyToMany(
            mappedBy = "students"
    )
    private List<Guardian> guardians;

}
