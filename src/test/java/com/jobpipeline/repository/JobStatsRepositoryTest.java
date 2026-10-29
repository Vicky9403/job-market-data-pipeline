package com.jobpipeline.repository;

import com.jobpipeline.dto.CitySalary;
import com.jobpipeline.dto.LocationCount;
import com.jobpipeline.dto.TopJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class JobStatsRepositoryTest {

    private JdbcTemplate jdbcTemplate;
    private JobStatsRepository repository;

    @BeforeEach
    void setUp() {
        jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        repository = new JobStatsRepository(jdbcTemplate);
    }

    @Test
    void testGetTotalJobs() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class))).thenReturn(8L);
        assertEquals(8L, repository.getTotalJobs());
    }

    @Test
    void testGetAverageSalary() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Double.class))).thenReturn(51625.0);
        assertEquals(51625.0, repository.getAverageSalary());
    }

    @Test
    void testGetAverageSalaryByCity() {
        List<CitySalary> expected = List.of(new CitySalary("Johannesburg", 61666.67));
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<RowMapper<CitySalary>>any()))
                .thenReturn(expected);

        List<CitySalary> result = repository.getAverageSalaryByCity();
        assertEquals(1, result.size());
        assertEquals("Johannesburg", result.get(0).city());
    }

    @Test
    void testGetTop5HighestPayingJobs() {
        List<TopJob> expected = List.of(new TopJob("Senior Data Engineer", "BigTech", "Johannesburg", 75000.0));
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<RowMapper<TopJob>>any()))
                .thenReturn(expected);

        List<TopJob> result = repository.getTop5HighestPayingJobs();
        assertEquals(1, result.size());
        assertEquals("Senior Data Engineer", result.get(0).title());
    }

    @Test
    void testGetJobCountsByLocation() {
        List<LocationCount> expected = List.of(new LocationCount("Johannesburg", 3));
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<RowMapper<LocationCount>>any()))
                .thenReturn(expected);

        List<LocationCount> result = repository.getJobCountsByLocation();
        assertEquals(1, result.size());
        assertEquals("Johannesburg", result.get(0).location());
    }
}
