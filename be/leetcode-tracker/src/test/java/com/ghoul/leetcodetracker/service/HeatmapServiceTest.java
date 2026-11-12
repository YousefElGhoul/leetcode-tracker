package com.ghoul.leetcodetracker.service;

import com.ghoul.leetcodetracker.model.entities.Heatmap;
import com.ghoul.leetcodetracker.repositories.HeatmapRepo;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HeatmapServiceTest {
    private final HeatmapRepo repo = mock(HeatmapRepo.class);
    private final HeatmapService service = new HeatmapService(repo);

    @Test
    void firstSnapshotEstablishesBaselineAndVisit() {
        when(repo.findByUsernameAndDate("alice", LocalDate.now())).thenReturn(Optional.empty());
        when(repo.findFirstByUsernameAndDateBeforeAndTotalSolvedIsNotNullOrderByDateDesc("alice", LocalDate.now()))
                .thenReturn(Optional.empty());
        when(repo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.upsertActivity("alice", 42);

        verify(repo).save(org.mockito.ArgumentMatchers.argThat(record ->
                record.isVisited() && !record.isSolved() && record.getTotalSolved() == 42));
    }

    @Test
    void sameDayIncreaseMarksSolvedAndPreservesHighestBaseline() {
        Heatmap today = record(40, false);
        when(repo.findByUsernameAndDate("alice", LocalDate.now())).thenReturn(Optional.of(today));

        service.upsertActivity("alice", 41);
        service.upsertActivity("alice", 39);

        assertThat(today.isSolved()).isTrue();
        assertThat(today.isVisited()).isTrue();
        assertThat(today.getTotalSolved()).isEqualTo(41);
    }

    @Test
    void visitOnlyRecordUsesPreviousSnapshotAsBaseline() {
        Heatmap today = record(null, false);
        Heatmap previous = record(10, false);
        when(repo.findByUsernameAndDate("alice", LocalDate.now())).thenReturn(Optional.of(today));
        when(repo.findFirstByUsernameAndDateBeforeAndTotalSolvedIsNotNullOrderByDateDesc("alice", LocalDate.now()))
                .thenReturn(Optional.of(previous));

        service.upsertActivity("alice", 11);

        assertThat(today.isSolved()).isTrue();
        assertThat(today.getTotalSolved()).isEqualTo(11);
    }

    private Heatmap record(Integer total, boolean solved) {
        Heatmap record = new Heatmap();
        record.setUsername("alice");
        record.setDate(LocalDate.now());
        record.setTotalSolved(total);
        record.setSolved(solved);
        return record;
    }
}
