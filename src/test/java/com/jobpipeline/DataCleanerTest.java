package com.jobpipeline;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataCleanerTest {

    @TempDir
    Path tempDir;

    @Test
    void testDataCleaningProducesExpectedOutput() throws Exception {
        DataCleaner cleaner = new DataCleaner();

        cleaner.cleanData();

        String outputFile = "data/output/cleaned_jobs.csv";

        try (CSVReader reader = new CSVReader(new FileReader(outputFile))) {

            List<String[]> records = reader.readAll();

            assertEquals(9, records.size(),
                    "The file should contain one header and eight job records");

            assertArrayEquals(
                    new String[]{
                            "job_id", "title", "company",
                            "location", "salary", "posted_date"
                    },
                    records.get(0),
                    "The output should have the correct CSV header"
            );

            assertEquals("1", records.get(1)[0]);
            assertEquals("Software Engineer", records.get(1)[1]);
            assertEquals("TechCorp", records.get(1)[2]);
            assertEquals("Johannesburg", records.get(1)[3]);
            assertEquals("45000", records.get(1)[4]);

            assertEquals("8", records.get(8)[0],
                    "The final job record should have job ID 8");
        }
    }

    @Test
    void testCleaningRemovesDuplicatesAndInvalidRecords() throws Exception {
        Path inputFile = tempDir.resolve("messy_jobs.csv");
        Path outputFile = tempDir.resolve("cleaned_jobs.csv");

        try (CSVWriter writer = new CSVWriter(new FileWriter(inputFile.toFile()))) {

            writer.writeNext(new String[]{
                    "job_id", "title", "company",
                    "location", "salary", "posted_date"
            });

            // Valid record containing unnecessary spaces.
            writer.writeNext(new String[]{
                    " 1 ", " Software Engineer ", " TechCorp ",
                    " Johannesburg ", " 45000 ", " 2026-10-01 "
            });

            // Duplicate job ID: this record should be removed.
            writer.writeNext(new String[]{
                    "1", "Duplicate Engineer", "OtherCompany",
                    "Pretoria", "50000", "2026-10-02"
            });

            // Missing title: this record should be rejected.
            writer.writeNext(new String[]{
                    "2", "", "DataCorp",
                    "Pretoria", "35000", "2026-10-03"
            });

            // Invalid salary: this record should be rejected.
            writer.writeNext(new String[]{
                    "3", "Java Developer", "CodeShop",
                    "Cape Town", "not-a-number", "2026-10-04"
            });

            // Negative salary: this record should be rejected.
            writer.writeNext(new String[]{
                    "4", "Cloud Engineer", "Cloud Africa",
                    "Johannesburg", "-1000", "2026-10-05"
            });

            // Another valid record.
            writer.writeNext(new String[]{
                    "5", "Data Analyst", "DataViz",
                    "Pretoria", "35000", "2026-10-06"
            });
        }

        DataCleaner cleaner = new DataCleaner(
                inputFile.toString(),
                outputFile.toString()
        );

        cleaner.cleanData();

        assertTrue(Files.exists(outputFile),
                "The cleaned output file should exist");

        try (CSVReader reader = new CSVReader(new FileReader(outputFile.toFile()))) {

            List<String[]> records = reader.readAll();

            // One header and two valid, unique job records.
            assertEquals(3, records.size(),
                    "The output should contain a header and two valid records");

            // Check that the header is correct.
            assertArrayEquals(
                    new String[]{
                            "job_id", "title", "company",
                            "location", "salary", "posted_date"
                    },
                    records.get(0)
            );

            // Check that spaces have been trimmed.
            assertEquals("1", records.get(1)[0]);
            assertEquals("Software Engineer", records.get(1)[1]);
            assertEquals("TechCorp", records.get(1)[2]);
            assertEquals("Johannesburg", records.get(1)[3]);
            assertEquals("45000", records.get(1)[4]);
            assertEquals("2026-10-01", records.get(1)[5]);

            // Check that the duplicate did not replace the first record.
            assertNotEquals("Duplicate Engineer", records.get(1)[1]);

            // Check that the second valid record was preserved.
            assertEquals("5", records.get(2)[0]);
            assertEquals("Data Analyst", records.get(2)[1]);
        }
    }
}