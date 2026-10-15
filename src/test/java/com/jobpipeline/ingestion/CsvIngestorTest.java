package com.jobpipeline.ingestion;

import com.jobpipeline.models.Job;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvIngestorTest {

    @Test
    @DisplayName("Should successfully load jobs from the valid CSV file")
    void testLoadJobsSuccess() {
        CsvIngestor ingestor = new CsvIngestor();
        String path = "data/input/jobs.csv";
        
        List<Job> jobs = ingestor.loadJobs(path);
        
        assertNotNull(jobs);
        assertFalse(jobs.isEmpty(), "The job list should not be empty");
        
        // Verify the first record matches data/input/jobs.csv exactly
        Job firstJob = jobs.get(0);
        assertEquals("1", firstJob.getJobId());
        assertEquals("Software Engineer", firstJob.getTitle());
        assertEquals("TechCorp", firstJob.getCompany());
        assertEquals("Johannesburg", firstJob.getLocation());
        assertEquals("45000", firstJob.getSalary());
    }

    @Test
    @DisplayName("Should return an empty list when the file does not exist")
    void testLoadJobsFileNotFound() {
        CsvIngestor ingestor = new CsvIngestor();
        String invalidPath = "data/input/non_existent.csv";
        
        List<Job> jobs = ingestor.loadJobs(invalidPath);
        
        assertNotNull(jobs);
        assertTrue(jobs.isEmpty(), "The job list should be empty for a missing file");
    }
}
