package com.pks.spring_sms_backend.entity;


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
public class School {
    @Id
    @SequenceGenerator(
            name="school_sequence",
            sequenceName = "school_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "school_sequence"
    )
    private Long schoolId;

    @NotNull(message = "school name cannot be null")
    @NotEmpty(message = "School Name field cannot be empty")
    private String name;


    @NotNull(message = "school address cannot be null")
    @NotEmpty(message = "School Address field cannot be empty")
    private String address;


    @NotBlank(message = "Phone Number is required")
    @Size(min = 10, max = 10)
    @Pattern(regexp = "^\\d{10}$")
    private String phone_no;

    private String school_logo;

    private String longitude;


    private String latitude;


    private LocalDate dateEstablished;

    @NotEmpty(message = "Email field cannot be empty")
    @Email(message = "Email field is not valid")
    @Size(max = 255,message = "Email field cannot be more than 255")
    private String email;


    private LocalDateTime dateCreated;


    private LocalDateTime dateUpdated;

    @OneToMany(
          mappedBy = "school"
    )
    private List<User> users ;

    @OneToMany(
            mappedBy = "school"
    )
    private List<Category> categories ;


    @OneToMany(
            mappedBy = "school"
    )
    private List<Klass> classes;


    @OneToMany(
            mappedBy = "school"
    )
    private List<Teacher> teachers;

    @OneToMany(
            mappedBy = "school"
    )
    private List<Student> students;

    @OneToMany(
            mappedBy = "school"
    )
    private List<Guardian> guardians;

}
