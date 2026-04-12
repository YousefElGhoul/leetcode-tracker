package com.ghoul.leetcodetracker.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Entity
@Table(
        name = "heatmap",
        uniqueConstraints = @UniqueConstraint(columnNames = {"username", "date"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Heatmap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private boolean visited = false;

    @Column(nullable = false)
    private boolean solved = false;

    @Column(nullable = false)
    private int totalSolved = 0;
}