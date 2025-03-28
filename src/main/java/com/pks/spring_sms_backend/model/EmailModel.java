package com.pks.spring_sms_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailModel {
    private String oldEmail;
    private String newEmail;
    private LocalDateTime dateUpdated = LocalDateTime.now();
}
