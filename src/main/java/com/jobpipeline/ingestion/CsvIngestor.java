package com.jobpipeline.ingestion;

import com.jobpipeline.models.Job;
import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for reading job data from a CSV file.
 */
public class CsvIngestor {
    private static final Logger logger = LoggerFactory.getLogger(CsvIngestor.class);

    /**
     * Reads a CSV file and converts each row into a Job object.
     *
     * @param filePath The path to the CSV file.
     * @return A list of Job objects.
     */
    public List<Job> loadJobs(String filePath) {
        List<Job> jobs = new ArrayList<>();

        try (FileReader reader = new FileReader(filePath)) {
            jobs = new CsvToBeanBuilder<Job>(reader)
                    .withType(Job.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();

            logger.info("Successfully loaded {} job records from {}", jobs.size(), filePath);
        } catch (IOException e) {
            logger.error("Error reading the CSV file: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred during ingestion: {}", e.getMessage());
        }

        return jobs;
    }

    public static void main(String[] args) {
        CsvIngestor ingestor = new CsvIngestor();
        String csvPath = "data/input/jobs.csv";
        List<Job> jobs = ingestor.loadJobs(csvPath);

        // Print the first few jobs to verify
        jobs.stream().limit(5).forEach(System.out::println);
    }
}
