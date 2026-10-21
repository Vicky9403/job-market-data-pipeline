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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DataCleaner is responsible for cleaning the job data.
 * It trims spaces, handles missing values, removes duplicates, and validates salaries.
 */
public class DataCleaner {

    private static final Logger logger = LoggerFactory.getLogger(DataCleaner.class);

    private static final String DEFAULT_INPUT_FILE = "data/input/jobs.csv";
    private static final String DEFAULT_OUTPUT_DIR = "data/output";
    private static final String DEFAULT_OUTPUT_FILE = DEFAULT_OUTPUT_DIR + "/cleaned_jobs.csv";

    private final String inputFile;
    private final String outputFile;
    private final String outputDir;

    // Default constructor uses the project's normal CSV files.
    public DataCleaner() {
        this(DEFAULT_INPUT_FILE, DEFAULT_OUTPUT_FILE);
    }

    // Constructor allows custom file paths for testing.
    public DataCleaner(String inputFile, String outputFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;

        Path parent = Paths.get(outputFile).getParent();
        this.outputDir = parent == null ? "." : parent.toString();
    }

    public void cleanData() {
        CsvIngestor ingestor = new CsvIngestor();
        List<Job> rawJobs = ingestor.loadJobs(inputFile);

        if (rawJobs.isEmpty()) {
            System.out.println("No records found in " + inputFile);
            return;
        }

        int totalRead = rawJobs.size();
        int duplicatesRemoved = 0;
        int invalidRecords = 0;

        // Maintain insertion order while removing duplicates by job_id.
        Map<String, Job> uniqueJobs = new LinkedHashMap<>();

        for (Job job : rawJobs) {

            // 1. Remove unnecessary spaces.
            trimJobFields(job);

            // 2. Skip records missing required values.
            if (isInvalid(job)) {
                invalidRecords++;
                continue;
            }

            // 3. Skip records with invalid salary values.
            if (!isValidSalary(job.getSalary())) {
                invalidRecords++;
                continue;
            }

            // 4. Remove duplicate records based on job_id.
            if (uniqueJobs.containsKey(job.getJobId())) {
                duplicatesRemoved++;
            } else {
                uniqueJobs.put(job.getJobId(), job);
            }
        }

        List<Job> cleanedJobs = new ArrayList<>(uniqueJobs.values());

        // 5. Create the output directory if it does not exist.
        if (!ensureOutputDirectoryExists()) {
            System.out.println("Could not create output directory: " + outputDir);
            return;
        }

        // 6. Save the cleaned data.
        if (!saveCleanedData(cleanedJobs)) {
            System.out.println("Could not save cleaned data to " + outputFile);
            return;
        }

        // 7. Print a summary.
        printSummary(totalRead, cleanedJobs.size(), invalidRecords, duplicatesRemoved);
    }

    private void trimJobFields(Job job) {
        if (job.getJobId() != null) {
            job.setJobId(job.getJobId().trim());
        }

        if (job.getTitle() != null) {
            job.setTitle(job.getTitle().trim());
        }

        if (job.getCompany() != null) {
            job.setCompany(job.getCompany().trim());
        }

        if (job.getLocation() != null) {
            job.setLocation(job.getLocation().trim());
        }

        if (job.getSalary() != null) {
            job.setSalary(job.getSalary().trim());
        }

        if (job.getPostedDate() != null) {
            job.setPostedDate(job.getPostedDate().trim());
        }
    }

    private boolean isInvalid(Job job) {
        return isNullOrEmpty(job.getJobId())
                || isNullOrEmpty(job.getTitle())
                || isNullOrEmpty(job.getCompany());
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    private boolean isValidSalary(String salary) {
        if (isNullOrEmpty(salary)) {
            return false;
        }

        try {
            double amount = Double.parseDouble(salary);
            return Double.isFinite(amount) && amount >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean ensureOutputDirectoryExists() {
        try {
            Path path = Paths.get(outputDir);

            if (!Files.exists(path)) {
                Files.createDirectories(path);
                logger.info("Created directory: {}", outputDir);
            }

            return Files.isDirectory(path);

        } catch (IOException e) {
            logger.error("Could not create output directory: {}", e.getMessage());
            return false;
        }
    }

    private boolean saveCleanedData(List<Job> jobs) {
        try (FileWriter writer = new FileWriter(outputFile);
             CSVWriter csvWriter = new CSVWriter(
                     writer,
                     CSVWriter.DEFAULT_SEPARATOR,
                     CSVWriter.NO_QUOTE_CHARACTER,
                     CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                     CSVWriter.DEFAULT_LINE_END)) {

            String[] header = {
                    "job_id",
                    "title",
                    "company",
                    "location",
                    "salary",
                    "posted_date"
            };

            csvWriter.writeNext(header);

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

            logger.info("Saved {} cleaned records to {}", jobs.size(), outputFile);
            return true;

        } catch (IOException e) {
            logger.error("Error saving cleaned data: {}", e.getMessage());
            return false;
        }
    }

    private void printSummary(int total, int valid, int invalid, int duplicates) {
        System.out.println("\n--- Data Cleaning Summary ---");
        System.out.println("Total records read:      " + total);
        System.out.println("Valid records kept:      " + valid);
        System.out.println("Invalid records skipped: " + invalid);
        System.out.println("Duplicates removed:      " + duplicates);
        System.out.println("-----------------------------\n");
    }

    public static void main(String[] args) {
        DataCleaner cleaner = new DataCleaner();
        cleaner.cleanData();
    }
}