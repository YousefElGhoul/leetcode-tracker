package com.ghoul.leetcodetracker.service;

import com.ghoul.leetcodetracker.model.dto.HeatmapRecord;
import com.ghoul.leetcodetracker.model.entities.Heatmap;
import com.ghoul.leetcodetracker.repositories.HeatmapRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HeatmapService {

    private final HeatmapRepo heatmapRepo;

    /**
       Upserts today's activity for the given user. On the first ever visit, we only establish
       a baseline totalSolved without marking anything as solved, since we have no reference point
       to determine what was solved today vs. before tracking began. On later visits, solved
       is determined by comparing the current total against the most recent snapshot in the DB,
       regardless of how long the user has been away.

       @param username     the LeetCode username to track
       @param currentTotal the current total number of problems solved fetched from LeetCode
     */
    @Transactional
    public void upsertActivity(String username, int currentTotal) {
        LocalDate today = LocalDate.now();
        Optional<Heatmap> todayRecord = heatmapRepo.findByUsernameAndDate(username, today);
        Optional<Integer> baseline = todayRecord.map(Heatmap::getTotalSolved)
                .or(() -> heatmapRepo
                        .findFirstByUsernameAndDateBeforeAndTotalSolvedIsNotNullOrderByDateDesc(username, today)
                        .map(Heatmap::getTotalSolved));
        Heatmap record = todayRecord.orElseGet(Heatmap::new);
        boolean solved = record.isSolved() || baseline.filter(total -> currentTotal > total).isPresent();

        record.setUsername(username);
        record.setDate(today);
        record.setSolved(solved);
        record.setVisited(true);
        record.setTotalSolved(Math.max(currentTotal, baseline.orElse(currentTotal)));
        heatmapRepo.save(record);
    }

    /**
     * Marks today's record as visited for the given user. If no record exists for today,
     * a new one is created with visited set to true but solved and totalSolved left at their
     * defaults, to avoid corrupting the baseline before upsertActivity is called.
     *
     * @param username the LeetCode username to track
     */
    @Transactional
    public void recordVisit(String username) {
        LocalDate today = LocalDate.now();

        Heatmap record = heatmapRepo
                .findByUsernameAndDate(username, today)
                .orElseGet(() -> {
                    Heatmap h = new Heatmap();
                    h.setUsername(username);
                    h.setDate(today);
                    return h;
                });

        record.setVisited(true);
        heatmapRepo.save(record);
    }

    public List<HeatmapRecord> getHeatmap(String username, LocalDate from, LocalDate to) {
        return heatmapRepo
                .findByUsernameAndDateBetweenOrderByDateAsc(username, from, to)
                .stream()
                .map(HeatmapRecord::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void clearHeatmap(String username) {
        heatmapRepo.deleteByUsername(username);
    }
}
