package com.atlas.userservice.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificationDto {
    private UUID id;
    private String name;
    private String issuer;
    private LocalDate issueDate;
    private String credentialUrl;
}
