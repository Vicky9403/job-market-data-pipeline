package com.jobpipeline.repository;

import com.jobpipeline.dto.CitySalary;
import com.jobpipeline.dto.LocationCount;
import com.jobpipeline.dto.TopJob;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository executing analytical queries directly against PostgreSQL jobs table.
 */
@Repository
public class JobStatsRepository {

    private final JdbcTemplate jdbcTemplate;

    public JobStatsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long getTotalJobs() {
        String sql = "SELECT COUNT(*) FROM jobs";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }

    public double getAverageSalary() {
        String sql = "SELECT COALESCE(ROUND(AVG(salary), 2), 0) FROM jobs";
        Double average = jdbcTemplate.queryForObject(sql, Double.class);
        return average != null ? average : 0.0;
    }

    public List<CitySalary> getAverageSalaryByCity() {
        String sql = """
                SELECT location, ROUND(AVG(salary), 2) AS average_salary
                FROM jobs
                GROUP BY location
                ORDER BY average_salary DESC
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new CitySalary(rs.getString("location"), rs.getDouble("average_salary"))
        );
    }

    public List<TopJob> getTop5HighestPayingJobs() {
        String sql = """
                SELECT title, company, location, salary
                FROM jobs
                ORDER BY salary DESC
                LIMIT 5
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new TopJob(
                        rs.getString("title"),
                        rs.getString("company"),
                        rs.getString("location"),
                        rs.getDouble("salary")
                )
        );
    }

    public List<LocationCount> getJobCountsByLocation() {
        String sql = """
                SELECT location, COUNT(*) AS total_jobs
                FROM jobs
                GROUP BY location
                ORDER BY total_jobs DESC, location ASC
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new LocationCount(rs.getString("location"), rs.getLong("total_jobs"))
        );
    }
}
