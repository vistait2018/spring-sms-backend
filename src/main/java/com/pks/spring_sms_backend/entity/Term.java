package com.pks.spring_sms_backend.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.pks.spring_sms_backend.enums.TermState;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Term {
    @Id
    @SequenceGenerator(
            name="term_sequence",
            sequenceName = "term_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "term_sequence"
    )
    private Long termId;

    @Enumerated(EnumType.STRING)
    private TermState term;


    private LocalDateTime dateCreated;


    private LocalDateTime dateUpdated;

}
