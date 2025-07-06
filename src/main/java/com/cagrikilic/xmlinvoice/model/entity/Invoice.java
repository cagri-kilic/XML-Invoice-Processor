package com.cagrikilic.xmlinvoice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nip", nullable = false)
    private String nip;

    @Column(name = "p1", nullable = false)
    private String p1;

    @Column(name = "p2", nullable = false)
    private String p2;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
} 