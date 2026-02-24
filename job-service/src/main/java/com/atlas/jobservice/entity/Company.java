package com.atlas.jobservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String industry;
    
    @Column(name = "logo_url")
    private String logoUrl;

    private String website;

    @Column(columnDefinition = "TEXT")
    private String description;

    private int size;

    @Column(name = "founded_year")
    private int foundedYear;
}
