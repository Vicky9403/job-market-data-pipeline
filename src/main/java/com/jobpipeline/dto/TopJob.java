package com.jobpipeline.dto;

/**
 * Details of a top-paying job.
 */
public record TopJob(
        String title,
        String company,
        String location,
        double salary
) {}
