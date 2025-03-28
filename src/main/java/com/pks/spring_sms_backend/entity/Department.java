package com.pks.spring_sms_backend.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
@Entity
public class Department {

    @Id
    @SequenceGenerator(
            name="department_sequence",
            sequenceName = "department_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "department_sequence"
    )
    private Long departmentId;

    @NotNull(message = "Department Name field cannot be null")
    @NotEmpty(message = "Department Name field cannot be empty")
    private String departmentName;

    private LocalDateTime dateCreated;


    private LocalDateTime dateUpdated;


    @ManyToOne(
            cascade =  CascadeType.ALL
    )
    @JoinColumn(
            name="category_id",
            referencedColumnName = "categoryId"
    )
    private Category category;


    @ManyToMany(
            mappedBy = "departments"
    )
    private List<Subject> subjects;

}
