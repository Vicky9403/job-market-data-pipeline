package com.jobpipeline;

import com.jobpipeline.ingestion.CsvIngestor;
import com.jobpipeline.models.Job;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * DataCleaner is responsible for cleaning the job data.
 * It trims spaces, handles missing values, removes duplicates, and validates salaries.
 */
public class DataCleaner {
    private static final Logger logger = LoggerFactory.getLogger(DataCleaner.class);

    private static final String INPUT_FILE = "data/input/jobs.csv";
    private static final String OUTPUT_DIR = "data/output";
    private static final String OUTPUT_FILE = OUTPUT_DIR + "/cleaned_jobs.csv";

    public void cleanData() {
        CsvIngestor ingestor = new CsvIngestor();
        List<Job> rawJobs = ingestor.loadJobs(INPUT_FILE);

        if (rawJobs.isEmpty()) {
            System.out.println("No records found in " + INPUT_FILE);
            return;
        }

        int totalRead = rawJobs.size();
        int duplicatesRemoved = 0;
        int invalidRecords = 0;

        // LinkedHashMap to maintain insertion order while removing duplicates by job_id
        Map<String, Job> uniqueJobs = new LinkedHashMap<>();

        for (Job job : rawJobs) {
            // 1. Trim unnecessary spaces
            trimJobFields(job);

            // 2. Handle missing required values (job_id, title, company)
            if (isInvalid(job)) {
                invalidRecords++;
                continue;
            }

            // 3. Validate salary values (must be a valid number)
            if (!isValidSalary(job.getSalary())) {
                invalidRecords++;
                continue;
            }

            // 4. Remove duplicate records based on job_id
            if (uniqueJobs.containsKey(job.getJobId())) {
                duplicatesRemoved++;
            } else {
                uniqueJobs.put(job.getJobId(), job);
            }
        }

        List<Job> cleanedJobs = new ArrayList<>(uniqueJobs.values());

        // 5. Create data/output/ if it does not exist
        ensureOutputDirectoryExists();

        // 6. Save the cleaned data
        saveCleanedData(cleanedJobs);

        // 7. Print simple summary
        printSummary(totalRead, cleanedJobs.size(), invalidRecords, duplicatesRemoved);
    }

    private void trimJobFields(Job job) {
        if (job.getJobId() != null) job.setJobId(job.getJobId().trim());
        if (job.getTitle() != null) job.setTitle(job.getTitle().trim());
        if (job.getCompany() != null) job.setCompany(job.getCompany().trim());
        if (job.getLocation() != null) job.setLocation(job.getLocation().trim());
        if (job.getSalary() != null) job.setSalary(job.getSalary().trim());
        if (job.getPostedDate() != null) job.setPostedDate(job.getPostedDate().trim());
    }

    private boolean isInvalid(Job job) {
        return isNullOrEmpty(job.getJobId()) ||
               isNullOrEmpty(job.getTitle()) ||
               isNullOrEmpty(job.getCompany());
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    private boolean isValidSalary(String salary) {
        if (isNullOrEmpty(salary)) return false;
        try {
            Double.parseDouble(salary);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void ensureOutputDirectoryExists() {
        try {
            Path path = Paths.get(OUTPUT_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                logger.info("Created directory: {}", OUTPUT_DIR);
            }
        } catch (IOException e) {
            logger.error("Could not create output directory: {}", e.getMessage());
        }
    }

    private void saveCleanedData(List<Job> jobs) {
        // Using CSVWriter directly to ensure we keep the header order and format simple
        try (FileWriter writer = new FileWriter(OUTPUT_FILE);
             CSVWriter csvWriter = new CSVWriter(writer,
                     CSVWriter.DEFAULT_SEPARATOR,
                     CSVWriter.NO_QUOTE_CHARACTER,
                     CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                     CSVWriter.DEFAULT_LINE_END)) {

            // Define the header
            String[] header = {"job_id", "title", "company", "location", "salary", "posted_date"};
            csvWriter.writeNext(header);

            // Write each job record
            for (Job job : jobs) {
                String[] row = {
                        job.getJobId(),
                        job.getTitle(),
                        job.getCompany(),
                        job.getLocation(),
                        job.getSalary(),
                        job.getPostedDate()
                };
                csvWriter.writeNext(row);
            }
            logger.info("Saved {} cleaned records to {}", jobs.size(), OUTPUT_FILE);
        } catch (IOException e) {
            logger.error("Error saving cleaned data: {}", e.getMessage());
        }
    }

    private void printSummary(int total, int valid, int invalid, int duplicates) {
        System.out.println("\n--- Data Cleaning Summary ---");
        System.out.println("Total records read:    " + total);
        System.out.println("Valid records kept:    " + valid);
        System.out.println("Invalid records skipped: " + invalid);
        System.out.println("Duplicates removed:    " + duplicates);
        System.out.println("-----------------------------\n");
    }

    public static void main(String[] args) {
        DataCleaner cleaner = new DataCleaner();
        cleaner.cleanData();
    }
}
