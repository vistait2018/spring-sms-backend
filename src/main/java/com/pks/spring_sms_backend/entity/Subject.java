package com.pks.spring_sms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Subject {

    @Id
    @SequenceGenerator(
            name="subject_sequence",
            sequenceName = "subject_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "subject_sequence"
    )
    private Long subjectId;

    @NotNull(message = "Subject Name field cannot be null")
    @NotEmpty(message = "Subject Name field cannot be empty")
    private String subjectName;

    private LocalDateTime dateCreated;


    private LocalDateTime dateUpdated;


    @ManyToMany(
            cascade = CascadeType.ALL
    )
    @JoinTable(
            name ="subject_departments",
            joinColumns = @JoinColumn(
                    name="subject_id",
                    referencedColumnName = "subjectId"
            ),
            inverseJoinColumns = @JoinColumn(
                    name="department_id",
                    referencedColumnName = "departmentId"
            )
    )
    private List<Department> departments;

    public void addDepartment(Department department){
        if(departments == null )  departments = new ArrayList<>();
        departments.add(department);

    }

}
