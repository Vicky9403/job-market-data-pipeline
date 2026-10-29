package com.jobpipeline.dto;

/**
 * Job count per location/city.
 */
public record LocationCount(
        String location,
        long count
) {}
