package com.jobpipeline.service;

import com.jobpipeline.dto.CitySalary;
import com.jobpipeline.dto.DashboardStats;
import com.jobpipeline.dto.LocationCount;
import com.jobpipeline.dto.TopJob;
import com.jobpipeline.repository.JobStatsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DashboardServiceTest {

    private JobStatsRepository repository;
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(JobStatsRepository.class);
        dashboardService = new DashboardService(repository);
    }

    @Test
    void testGetDashboardStatsAggregatesCorrectly() {
        when(repository.getTotalJobs()).thenReturn(8L);
        when(repository.getAverageSalary()).thenReturn(51625.0);
        when(repository.getAverageSalaryByCity()).thenReturn(List.of(
                new CitySalary("Johannesburg", 61666.67),
                new CitySalary("Cape Town", 55000.0)
        ));
        when(repository.getTop5HighestPayingJobs()).thenReturn(List.of(
                new TopJob("Senior Data Engineer", "BigTech", "Johannesburg", 75000.0),
                new TopJob("Cloud Engineer", "Cloud Africa", "Johannesburg", 65000.0)
        ));
        when(repository.getJobCountsByLocation()).thenReturn(List.of(
                new LocationCount("Johannesburg", 3),
                new LocationCount("Cape Town", 2)
        ));

        DashboardStats stats = dashboardService.getDashboardStats();

        assertNotNull(stats);
        assertEquals(8L, stats.totalJobs());
        assertEquals(51625.0, stats.averageSalary());
        assertEquals(2, stats.averageSalaryByCity().size());
        assertEquals("Johannesburg", stats.averageSalaryByCity().get(0).city());
        assertEquals(2, stats.topJobs().size());
        assertEquals("Senior Data Engineer", stats.topJobs().get(0).title());
        assertEquals(2, stats.jobsByLocation().size());

        verify(repository, times(1)).getTotalJobs();
        verify(repository, times(1)).getAverageSalary();
        verify(repository, times(1)).getAverageSalaryByCity();
        verify(repository, times(1)).getTop5HighestPayingJobs();
        verify(repository, times(1)).getJobCountsByLocation();
    }
}
