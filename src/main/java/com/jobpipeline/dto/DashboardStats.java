package com.jobpipeline.dto;

import java.util.List;

/**
 * Aggregated statistics payload for the Job Market dashboard.
 */
public record DashboardStats(
        long totalJobs,
        double averageSalary,
        List<CitySalary> averageSalaryByCity,
        List<TopJob> topJobs,
        List<LocationCount> jobsByLocation
) {}
