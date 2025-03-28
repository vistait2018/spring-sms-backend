package com.pks.spring_sms_backend.entity;


import com.pks.spring_sms_backend.enums.TermStatus;
import com.pks.spring_sms_backend.enums.YearStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class Year {
    @Id
    @SequenceGenerator(
            name="year_sequence",
            sequenceName = "year_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "year_sequence"
    )
    private Long yearId;


    @NotBlank(message = "Start year is required")
    @Size(min = 4, max = 4)
    @Pattern(regexp = "^\\d{4}$")
    private String startYear;


    @NotBlank(message = "End year is required")
    @Size(min = 4, max = 4)
    @Pattern(regexp = "^\\d{4}$")
    private String endYear;


    @Enumerated(EnumType.STRING)
    private YearStatus yearStatus;


    @Enumerated(EnumType.STRING)
    private TermStatus termStatus;

    private LocalDateTime dateCreated;


    private LocalDateTime dateUpdated;


}
