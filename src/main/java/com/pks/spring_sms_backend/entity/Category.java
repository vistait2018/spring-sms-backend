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
public class Category {

    @Id
    @SequenceGenerator(
            name="category_sequence",
            sequenceName = "category_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "category_sequence"
    )
    private Long categoryId;

    @NotNull(message = "Category Name field cannot be null")
    @NotEmpty(message = "Category Name field cannot be empty")
    private String categoryName;

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


    @OneToMany(
            mappedBy = "category"
    )
    private List<Department> departments;
}
