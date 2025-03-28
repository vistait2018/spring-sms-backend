package com.pks.spring_sms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Klass {
    @Id
    @SequenceGenerator(
            name="klass_sequence",
            sequenceName = "klass_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "klass_sequence"
    )
    private Long klassId;

    @NotNull(message = "Class Name field cannot be null")
    @NotEmpty(message = "Class Name field cannot be empty")
    private String klassName;

    private LocalDateTime dateCreated;


    private LocalDateTime dateUpdated;

    @ManyToOne(
            cascade =  CascadeType.ALL
    )
    @JoinColumn(
            name ="school_id",
            referencedColumnName = "schoolId"
    )
    private School school;


    @OneToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(
            name="teacher_id",
            referencedColumnName = "teacherId"
    )
    private Teacher classTeacher;

}
