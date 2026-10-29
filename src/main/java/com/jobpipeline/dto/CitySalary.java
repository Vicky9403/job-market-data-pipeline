package com.jobpipeline.dto;

/**
 * Average salary by city/location.
 */
public record CitySalary(
        String city,
        double averageSalary
) {}
