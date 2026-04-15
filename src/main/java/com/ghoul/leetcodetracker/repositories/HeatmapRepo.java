package com.ghoul.leetcodetracker.repositories;

import com.ghoul.leetcodetracker.model.entities.Heatmap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HeatmapRepo extends JpaRepository<Heatmap, Long> {

    Optional<Heatmap> findByUsernameAndDate(String username, LocalDate date);

    Optional<Heatmap> findFirstByUsernameAndDateBeforeOrderByDateDesc(String username, LocalDate date);

    List<Heatmap> findByUsernameAndDateBetweenOrderByDateAsc(
            String username,
            LocalDate from,
            LocalDate to
    );

    void deleteByUsername(String username);
}