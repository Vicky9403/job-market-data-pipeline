package com.jobpipeline.service;

import com.jobpipeline.dto.CitySalary;
import com.jobpipeline.dto.DashboardStats;
import com.jobpipeline.dto.LocationCount;
import com.jobpipeline.dto.TopJob;
import com.jobpipeline.repository.JobStatsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service aggregating job market metrics.
 */
@Service
public class DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);

    private final JobStatsRepository repository;

    public DashboardService(JobStatsRepository repository) {
        this.repository = repository;
    }

    public DashboardStats getDashboardStats() {
        logger.info("Fetching job market statistics from PostgreSQL...");

        long totalJobs = repository.getTotalJobs();
        double avgSalary = repository.getAverageSalary();
        List<CitySalary> salaryByCity = repository.getAverageSalaryByCity();
        List<TopJob> topJobs = repository.getTop5HighestPayingJobs();
        List<LocationCount> jobsByLocation = repository.getJobCountsByLocation();

        return new DashboardStats(
                totalJobs,
                avgSalary,
                salaryByCity,
                topJobs,
                jobsByLocation
        );
    }
}
