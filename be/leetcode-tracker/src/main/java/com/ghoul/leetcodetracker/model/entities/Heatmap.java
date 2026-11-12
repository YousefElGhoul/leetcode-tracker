package com.ghoul.leetcodetracker.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Entity
@Table(
        name = "heatmap",
        uniqueConstraints = @UniqueConstraint(columnNames = {"username", "date"})
)
@Getter
@Setter
@NoArgsConstructor
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

    private Integer totalSolved;
}
